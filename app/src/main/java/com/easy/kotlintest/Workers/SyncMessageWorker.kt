package com.easy.kotlintest.Workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.easy.kotlintest.Helper.Application
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.Constants.BASE_URL
import com.easy.kotlintest.Networking.Helper.Constants.GET_ALL_CHATS
import com.easy.kotlintest.Response.Chats_By_Thread.AllChatResponse
import com.easy.kotlintest.Response.Chats_By_Thread.ChatsItem
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Messages.Message_View_Model
import com.easy.kotlintest.Room.Thread.Thread_ViewModel
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SyncMessageWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    val con = appContext;
    override fun doWork(): Result {
        return try {


            val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            val resData: Data = Data.EMPTY
            val message_view_model = Message_View_Model(Application().getapplication())
            val thread_viewModel = Thread_ViewModel(Application().getapplication())
            val uthHeader: String = PrefUtill.getUser()?.user?.email ?: "";
            val map = HashMap<String, Any>()
            map["id"] = PrefUtill.getUser()?.getUser()?.getId()?:""
            apiInterface.PostRequestFormData(
                BASE_URL + GET_ALL_CHATS,
                uthHeader,
                map
            ).enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, res: Response<String?>) {
                    try {
                        val jsonObject = JSONObject(res.body().toString())
                        if (!jsonObject.has("error")) {
                            val gson = Gson()
                            val response: AllChatResponse =
                                gson.fromJson(jsonObject.toString(), AllChatResponse::class.java)
                            if (response != null) {
                                for (i in 0 until response.chats.size) {
                                    try {
                                        val item: ChatsItem = response.chats[i]
                                        val chats = Chats(
                                            item.attachment,
                                            item.sender,
                                            item.createdAt,
                                            item.id,
                                            item.thread,
                                            item.message,
                                            item.type,
                                            item.reciver,
                                            item.seen,
                                            item.replay_of,
                                            item.is_deleted
                                        )

                                            message_view_model.selectChat(item.id) { isss ->
                                                if (isss != null) {
                                                    if (!item.seen.equals(isss.seen) && item.seen
                                                            .toInt() > (isss.seen?.toInt() ?: 0)
                                                    ) {
                                                        message_view_model.updateSeen(
                                                            item.seen,
                                                            item.id
                                                        )
                                                    }
                                                    if (!item.is_deleted.equals(isss.deleted)) {
                                                        message_view_model.updateDelete(
                                                            item.id,
                                                            item.is_deleted
                                                        )
                                                    }
                                                } else {
                                                    message_view_model.insert(chats)
                                                    thread_viewModel.updateLastSeen(
                                                        chats.message,
                                                        chats.seen,
                                                        chats.type,
                                                        chats.thread,
                                                        chats.createdAt
                                                    )
                                                    if (i == response.chats.size - 1) {
                                                        val id: String =
                                                            PrefUtill.getUser()?.user?.id ?: ""
                                                        val sql =
                                                            "update `chats` set `seen` = '2' where `seen` = '1'  AND `reciver` =$id"
                                                        /*easysent.`in`.Helper.SyncData.sync_BY_SQL(
                                                            sql,
                                                            chats,
                                                            application,
                                                            handler
                                                        )*/
                                                    }
                                                }
                                            }

                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                   t.printStackTrace()
                }
            })




            return Result.success(resData)

        } catch (e: Exception) {
            // If there's an error, you might want to retry depending on the error
            Result.retry()
        }
    }

}