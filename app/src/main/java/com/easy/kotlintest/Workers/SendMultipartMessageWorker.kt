package com.easy.kotlintest.Workers

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.work.*
import com.easy.kotlintest.Encription.Encripter
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.Constants.BASE_URL
import com.easy.kotlintest.Networking.Helper.Constants.SEND_MESSAGE
import com.easy.kotlintest.Networking.Helper.ProgressRequestBody
import com.easy.kotlintest.Networking.Helper.multipart.Utils.getParam
import com.easy.kotlintest.Room.Messages.Chatepository
import com.easy.kotlintest.Room.Thread.Thread_repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.File


@Suppress("NAME_SHADOWING")
class SendMultipartMessageWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams),CoroutineScope {
    val con = appContext;
    override suspend fun doWork(): Result {
        return try {


            val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            var resData: Data = Data.EMPTY
            val repository = Chatepository(con)
            val threadRepository = Thread_repository(con)
            val uthHeader: String = PrefUtill.getUser()?.user?.email ?: "";
            val filePath = inputData.getString("multipart") ?: ""
            val file = File(filePath);
            val key = inputData.getString("key") ?: ""
            val map: HashMap<String, Any> = HashMap()
            var progress = 0;
            inputData.keyValueMap.iterator().forEach {
                map[it.key] = it.value
            }


            /*ddddd*/


            val mimeTypesMap = MimeTypeMap.getSingleton()

            val extention = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath())
            val mimeType = mimeTypesMap.getMimeTypeFromExtension(extention)
            val fileBody = ProgressRequestBody(file, mimeType,
            object : ProgressRequestBody.UploadCallbacks {
                override  fun onProgressUpdate(percentage: Int) {
                    progress = percentage;
                   // Log.e("progress", "progress update: $progress")
                    setProgressAsync(workDataOf("progress" to progress))
                   /* launch {
                        setProgress(workDataOf("progress" to progress))
                    }*/
                }

                override fun onError() {

                    progress = -1
                    setProgressAsync(workDataOf("progress" to progress))
                    //Log.e("progress", "progress update : $progress")

                }

                override fun onFinish() {
                       progress = 100
                    setProgressAsync(workDataOf("progress" to progress))
                    //Log.e("progress", "progress finish: $progress")
                    /*launch {
                        setProgress(workDataOf("progress" to progress))
                    }*/
                }
            })



            val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(key, file.name, fileBody)



            apiInterface.PostRequestMultipart( BASE_URL + SEND_MESSAGE, uthHeader, filePart, getParam(map))
                .execute().body().also {
                    try {

                        val obj = JSONObject(it?:"")
                        if (!obj.has("error")){
                            repository.updateSeen("1", map["m_id"].toString())
                        }




                        resData = Data.Builder().putAll(map)
                            .putString("res", it?:"")
                            .build()


                        if (map["is_1st"] as Boolean || map["thread"].toString()=="" ){

                            val syncThread = OneTimeWorkRequestBuilder<SyncThreadWorker>()
                                .build()
                            val manager = WorkManager
                                .getInstance(con)
                            manager.enqueue(syncThread)
                        }else{
                            val encripter = Encripter(map["sender"].toString())
                            threadRepository.update_last_seen(encripter.decrepit(map["message"].toString()),
                                "1",map["type"].toString(),map["is_1st"].toString(),map["date"].toString())
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        repository.updateSeen("1", map["m_id"].toString())
                        Log.e("TAG", "Exception: $it" )
                    }
                }

            /*dddddd*/





            return Result.success(resData)

        } catch (e: Exception) {
            // If there's an error, you might want to retry depending on the error
            Log.e("progress", "progress ---: RETRY")
            Result.retry()
        }
    }

}