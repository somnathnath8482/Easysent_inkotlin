package com.easy.kotlintest.Networking

import android.app.Activity
import com.easy.kotlintest.Networking.Helper.ApiClient
import com.easy.kotlintest.Networking.Helper.ApiInterface
import com.easy.kotlintest.Networking.Helper.MethodClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.easy.kotlintest.Networking.Interface.OnSuccess as Customresponse


open class Network {
    private val TAG: String? = "";
    private var apiInterface: ApiInterface = ApiClient.getClient().create(ApiInterface::class.java)
    private var job: Job = Job()
    private var scope: CoroutineScope = CoroutineScope(job)


    fun post(
        url: String,
        uthHeader: String = "",
        map: HashMap<String, Any>,
        customresponse: Customresponse
    ) {
        scope.launch {                          //non blocking corotines

            apiInterface.PostRequestFormData(url, uthHeader, map)
                .enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.code().toString() == "200") run {
                            try {
                                val jsonObject: JSONObject = JSONObject(response.body().toString())
                                if (jsonObject.has("error")) {
                                } else {
                                    customresponse.OnSucces(
                                        call.request().url.toString(),
                                        response.code().toString(),
                                        response.body()
                                    )
                                }


                            } catch (e: Exception) {
                            }

                        }


                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        customresponse.OnSucces(call.request().url.toString(), "-1", t.message)
                    }
                })
        }
    }

    fun post(
        should_show_errror: Boolean,
        context: Activity,
        url: String,
        uthHeader: String = "",
        map: HashMap<String, Any>,
        customresponse: Customresponse
    ) {
        scope.launch {                          //non blocking corotines

            apiInterface.PostRequestFormData(url, uthHeader, map)
                .enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.code().toString() == "200") run {
                            try {
                                val jsonObject = JSONObject(response.body().toString())
                                if (jsonObject.has("error")) {
                                    if (should_show_errror){
                                        MethodClass.hasError(context,com.easy.kotlintest.Response.Error.Error("1",jsonObject.getString("error").toString()))

                                    }

                                } else {
                                    customresponse.OnSucces(
                                        call.request().url.toString(),
                                        response.code().toString(),
                                        response.body()
                                    )
                                }


                            } catch (e: Exception) {
                            }

                        }


                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        customresponse.OnSucces(call.request().url.toString(), "-1", t.message)
                    }
                })
        }
    }


}