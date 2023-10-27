package com.easy.kotlintest.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Network
import com.easy.kotlintest.R
import com.easy.kotlintest.Response.Login.LoginResponse
import com.easy.kotlintest.Workers.SyncMessageWorker
import com.easy.kotlintest.Workers.SyncThreadWorker
import com.easy.kotlintest.Workers.SyncUserWorker
import com.easy.kotlintest.databinding.ActivityLoginBinding
import com.easy.kotlintest.socket.LiveMessage
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var activity: Activity
    private lateinit var network: Network
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        if (PrefUtill.getIsLogged()) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("already_login", true)
            startActivity(intent)
            finishAffinity()
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        network = Network()
        init()
    }

    private fun init() {
        binding.btnLogin.setOnClickListener {
            login(
                binding.edUsername.text.toString().trim(),
                binding.edPassword.text.toString().trim()
            )
        }

        binding.btnRegister.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignupActivity::class.java
                )
            )
        }
        binding.joinEasysent.setOnClickListener { binding.btnRegister.performClick() }

        binding.btnForgot.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ForgotPasswordActivity::class.java
                )
            )
        }

    }

    private fun login(uname: String, password: String) {

        val map = HashMap<String, Any>()
        map["password"] = password
        map["username"] = uname
        map["token"] = ""
        map["device"] = Build.MANUFACTURER
        map["model"] = Build.MODEL
        map["device_id"] = Build.ID


        network.post(
            true,
            activity,
            Constants.BASE_URL + Constants.LOGIN,
            map = map
        ) { url, code, res ->
            run {

                if (code.equals("200")) {
                    try {
                        val json = JSONObject(res)
                        if (json.has("error")) {
                            val error = com.easy.kotlintest.Response.Error.Error(
                                json.getString("code"),
                                json.getString("error")
                            )
                            MethodClass.hasError(activity, error)
                        } else {

                            if (json.getString("code") == "-100") {
                                StyleableToast.makeText(
                                    applicationContext,
                                    json.getString("message"),
                                    Toast.LENGTH_SHORT,
                                    R.style.mytoast
                                ).show()
                                val intent = Intent(applicationContext, OtpActivity::class.java)
                                intent.putExtra("otp", "")
                                intent.putExtra("from", "register")
                                intent.putExtra("email", json.getString("email"))
                                startActivity(intent)
                            } else if (json.getString("code") == "-200") {


                                val gson = Gson()
                                val loginResponse: LoginResponse = gson.fromJson(
                                    res.toString(),
                                    LoginResponse::class.java
                                )

                                StyleableToast.makeText(
                                    applicationContext,
                                    loginResponse.message,
                                    Toast.LENGTH_SHORT,
                                    R.style.mytoast
                                ).show()

                                PrefUtill.setIsLogged(true)
                                PrefUtill.setUser(loginResponse)

                                val syncUser = OneTimeWorkRequestBuilder<SyncUserWorker>()
                                    .build()

                                val syncThread = OneTimeWorkRequestBuilder<SyncThreadWorker>()
                                    .build()
                                val syncuser = OneTimeWorkRequestBuilder<SyncMessageWorker>()
                                    .build()

                                val requests: List<WorkRequest> = listOf(syncUser, syncThread, syncuser)

                                val manager = WorkManager
                                    .getInstance(this@LoginActivity)
                                manager.enqueue(requests)

                                LiveMessage(application)
                                LiveMessage.listen(PrefUtill.getUser()?.user?.id)

                                val intent = Intent(applicationContext, MainActivity::class.java)
                                //intent.putExtra("new_login", true)
                                startActivity(intent)
                                finishAffinity()
                            }

                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "login: $url " + e.message)
                    }


                }


            }
        }

    }


}