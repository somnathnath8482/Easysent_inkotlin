package com.easy.kotlintest.Networking

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.Constants.BASE_URL
import com.easy.kotlintest.Networking.Helper.Constants.GET_ALL_USER
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Response.AllUsers.AllUsersResponse
import com.easy.kotlintest.Response.AllUsers.UsersItem
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.Room.Users.Users
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.easy.kotlintest.Networking.Interface.OnSuccess as Customresponse


open class Sync {
    private val TAG: String? = "";
    private var apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
    private var job: Job = Job()
    private var scope: CoroutineScope = CoroutineScope(job)


    fun syncUser(provider: FragmentActivity) {
        scope.launch {

            //non blocking corotines
            var userVewModel = UserVewModel(provider.application)
            val uthHeader: String = PrefUtill.getUser()?.user?.email ?: "";
            apiInterface.PostRequestFormData(
                BASE_URL + GET_ALL_USER,
                uthHeader,
                java.util.HashMap()
            )
                .enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.code().toString() == "200") run {
                            try {
                                val jsonObject: JSONObject = JSONObject(response.body().toString())
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
                                                uitem.getToken())

                                                userVewModel.selectUser(uitem.getId(),object : Item<Users> {
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


                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {

                    }
                })
        }
    }



}