package com.easy.kotlintest.Workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.Constants.BASE_URL
import com.easy.kotlintest.Networking.Helper.Constants.GET_ALL_CHATS
import com.easy.kotlintest.Response.Chats_By_Thread.AllChatResponse
import com.easy.kotlintest.Response.Chats_By_Thread.ChatsItem
import com.easy.kotlintest.Room.Messages.Chatepository
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Thread.Thread_repository
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
            val repository = Chatepository(con)
            val threadRepository = Thread_repository(con)
            val uthHeader: String = PrefUtill.getUser()?.user?.email ?: "";
            val map = HashMap<String, Any>()
            map["id"] = PrefUtill.getUser()?.user?.id ?:""
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
                                        val item: ChatsItem = response.chats[i];
                                        val chats = Chats(
                                            item.attachment,
                                            item.sender,
                                            "",
                                            item.createdAt,
                                            item.id,
                                            item.thread,
                                            item.message,
                                            item.type,
                                            item.reciver,
                                            item.seen,
                                            null,
                                            item.replay_of,
                                            item.is_deleted
                                        )

                                            repository.selectChat(item.id) { isss ->
                                                if (isss != null) {
                                                    if (!item.seen.equals(isss.seen) && item.seen
                                                            .toInt() > (isss.seen?.toInt() ?: 0)
                                                    ) {
                                                        repository.updateSeen(
                                                            item.seen,
                                                            item.id
                                                        )
                                                    }
                                                    if (!item.is_deleted.equals(isss.deleted)) {
                                                        repository.updateDelete(
                                                            item.id,
                                                            item.is_deleted
                                                        )
                                                    }
                                                } else {
                                                    repository.insert(chats)
                                                    threadRepository.update_last_seen(
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