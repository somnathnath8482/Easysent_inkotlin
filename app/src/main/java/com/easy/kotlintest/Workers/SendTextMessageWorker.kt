package com.easy.kotlintest.Workers

import android.content.Context
import android.widget.Toast
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.Constants.*
import com.easy.kotlintest.Response.Chats_By_Thread.AllChatResponse
import com.easy.kotlintest.Room.Messages.Chatepository
import com.easy.kotlintest.Room.Thread.Thread_repository
import com.google.gson.Gson
import org.json.JSONObject


class SendTextMessageWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    val con = appContext;
    override fun doWork(): Result {
        return try {


            val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            var resData: Data = Data.EMPTY
            val repository = Chatepository(con)
            val threadRepository = Thread_repository(con)
            val uthHeader: String = PrefUtill.getUser()?.user?.email ?: "";
            val map: HashMap<String, Any> = HashMap()
            inputData.keyValueMap.iterator().forEach {
                map[it.key] = it.value
            }
            apiInterface.PostRequestFormData(
                BASE_URL + SEND_MESSAGE,
                uthHeader,
                map
            ).execute().body().also {
                try {

                    val obj = JSONObject(it?:"")
                    if (!obj.has("error")){
                        repository.updateSeen("1", map["m_id"].toString())
                    }


                        resData = Data.Builder().putAll(map)
                            .putString("res", it?:"")
                            .build()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }




            return Result.success(resData)

        } catch (e: Exception) {
            // If there's an error, you might want to retry depending on the error
            Result.retry()
        }
    }

}