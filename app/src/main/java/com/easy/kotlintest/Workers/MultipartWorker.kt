package com.easy.kotlintest.Workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.multipart.PART
import com.easy.kotlintest.Networking.Helper.multipart.Params
import com.easy.kotlintest.Networking.Helper.multipart.Utils
import org.json.JSONObject
import java.io.File


class MultipartWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return try {


            val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            val url = inputData.getString("url") ?: ""
            val profilePicPath = inputData.getString("multipart") ?: ""
            val map: HashMap<String, Any> = HashMap()

            inputData.keyValueMap.iterator().forEach {
                map[it.key] = it.value
            }
            val key = inputData.getString("key") ?: ""
            var resData: Data = Data.EMPTY


            apiInterface.PostRequestMultipart(
                url,
                "",
                Params.createMultiPart(PART(key, File(profilePicPath))),
                Utils.getParam(map)
            ).execute().body().also {
                try {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.has("error")) {


                    } else {
                        resData = Data.Builder().putAll(map)
                            .putString("res", it)
                            .build()
                    }


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