package com.easy.kotlintest.Workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.easy.kotlintest.Helper.Application
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.Constants.*
import com.easy.kotlintest.Response.AllThreads.AllThreadsResponse
import com.easy.kotlintest.Response.AllThreads.ThreadsItem
import com.easy.kotlintest.Room.Thread.Message_Thread
import com.easy.kotlintest.Room.Thread.Thread_ViewModel
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SyncThreadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return try {
            val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            val resData: Data = Data.EMPTY
            val thread_viewModel = Thread_ViewModel(Application().getapplication())
            val uthHeader: String = PrefUtill.getUser()?.user?.email ?: "";
            val id: String = PrefUtill.getUser()?.user?.id ?: "";
            val map = HashMap<String, Any>()
            map["id"] = id
            apiInterface.PostRequestFormData(
                BASE_URL + GET_ALL_THREAD,
                uthHeader,
                map
            ).enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, res: Response<String?>) {
                    try {
                        val jsonObject = JSONObject(res.body().toString())
                        if (!jsonObject.has("error")) {
                            val gson = Gson()
                            val response: AllThreadsResponse =
                                gson.fromJson(res.toString(), AllThreadsResponse::class.java)
                            if (response != null) {
                                for (i in 0 until response.threads.size) {
                                    try {
                                        val uitem: ThreadsItem = response.threads[i]
                                        val message_thread = Message_Thread(
                                            uitem.sender,
                                            uitem.id,
                                            uitem.createAt,
                                            uitem.reciver,
                                            "",
                                            "",
                                            "",
                                            uitem.createAt
                                        )
                                        thread_viewModel.insert(message_thread)
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    t.printStackTrace()
                }
            })




            return Result.success(resData)

        } catch (e: Exception) {
            // If there's an error, you might want to retry depending on the error
            Result.failure()
        }
    }

}