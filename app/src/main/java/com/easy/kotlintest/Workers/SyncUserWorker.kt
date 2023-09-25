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
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Response.AllUsers.AllUsersResponse
import com.easy.kotlintest.Response.AllUsers.UsersItem
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.Room.Users.Users
import com.google.gson.Gson
import org.json.JSONObject


class SyncUserWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return try {


            val apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            val resData: Data = Data.EMPTY
            val userVewModel = UserVewModel(Application().getapplication())
            val uthHeader: String = PrefUtill.getUser()?.user?.email ?: "";

            apiInterface.PostRequestFormData(
                Constants.BASE_URL + Constants.GET_ALL_USER,
                uthHeader,
                java.util.HashMap()
            ).execute().body().also {
                try {
                    val jsonObject = JSONObject(it.toString())
                    if (jsonObject.has("error")) {
                    } else {
                        val gson = Gson()
                        val response: AllUsersResponse = gson.fromJson(
                            jsonObject.toString(),
                            AllUsersResponse::class.java
                        )
                        for (i in 0 until response.users.size) {
                            try {
                                val uitem: UsersItem =
                                    response.getUsers().get(i)
                                val users = Users(
                                    uitem.getPhone(),
                                    uitem.getName(),
                                    uitem.getProfilePic(),
                                    uitem.getId(),
                                    "offline",
                                    uitem.getGender(),
                                    uitem.getAbout(),
                                    uitem.getIsVerifyed(),
                                    uitem.getEmail(),
                                    uitem.getToken()
                                );

                                userVewModel.selectUser(uitem.getId(),
                                    object : Item<Users> {
                                        override fun onItem(isss: Users?) {
                                            if (isss != null) {
                                                if (uitem.getToken() != null) {
                                                    if (!uitem.getToken()
                                                            .equals(isss.token)
                                                        || !uitem.getProfilePic()
                                                            .equals(isss.profilePic)
                                                        || !uitem.getName()
                                                            .equals(isss.name)
                                                    ) {
                                                        userVewModel.update(
                                                            uitem.getPhone(),
                                                            uitem.getName(),
                                                            uitem.getProfilePic(),
                                                            uitem.getGender(),
                                                            uitem.getAbout(),
                                                            uitem.getIsVerifyed(),
                                                            uitem.getEmail(),
                                                            uitem.getToken()
                                                        )
                                                    }
                                                }
                                            } else {
                                                userVewModel.insert(users)
                                            }
                                        }
                                    })


                                // userVewModel.insert(users);
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }


                    }


                } catch (e: Exception) {
                }
            }




            return Result.success(resData)

        } catch (e: Exception) {
            // If there's an error, you might want to retry depending on the error
            Result.retry()
        }
    }

}