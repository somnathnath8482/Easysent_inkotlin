package com.easy.kotlintest.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Network
import com.easy.kotlintest.R
import com.easy.kotlintest.Response.Register.RegisterResponse
import com.easy.kotlintest.databinding.ActivityForgotPasswordBinding
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var network: Network
    private lateinit var binding: ActivityForgotPasswordBinding
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        activity  =this
        setContentView(binding.root)
        network = Network()
        init()
    }

    private fun init() {

        binding.btnSendOtp.setOnClickListener {
            //  startActivity(new Intent(this,OtpActivity.class));
            val map =
                HashMap<String, Any>()
            try {
                map["email"] = MethodClass.CheckEmpty(binding.edEmail)
            } catch (e: Exception) {
                val editText: EditText = binding.root.findViewById(e.message!!.toInt())
                editText.requestFocus()
                e.printStackTrace()
                return@setOnClickListener
            }
            send(map)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    SignupActivity::class.java
                )
            )
        }
    }


    private fun send(map: java.util.HashMap<String, Any>) {

        network.post(true,activity,
            Constants.BASE_URL + Constants.FORGOT_PASSWORD,
            "",
            map
        ) { _, code, res ->
            kotlin.run {
                if (code == "200") {
                    if (res != null) {
                        val json = Gson()
                        val registerResponse: RegisterResponse = json.fromJson(
                            res.toString(),
                            RegisterResponse::class.java
                        )
                        StyleableToast.makeText(
                            applicationContext,
                            registerResponse.success,
                            Toast.LENGTH_SHORT,
                            R.style.mytoast
                        ).show()
                        val intent = Intent(applicationContext, OtpActivity::class.java)
                        intent.putExtra("otp", registerResponse.otp)
                        intent.putExtra("from", "forgot")
                        intent.putExtra("email", binding.edEmail.text.toString().trim())
                        startActivity(intent)
                    }
                }


            }
        }

    }
}