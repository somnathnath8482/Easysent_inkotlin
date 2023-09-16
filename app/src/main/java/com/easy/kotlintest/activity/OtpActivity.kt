package com.easy.kotlintest.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easy.kotlintest.Helper.GenericTextWatcher
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Network
import com.easy.kotlintest.R
import com.easy.kotlintest.Response.OtpResend.ResendOtpResponse
import com.easy.kotlintest.databinding.ActivityOtpBinding
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast
import org.json.JSONObject

class OtpActivity : AppCompatActivity() {
  private  lateinit var binding: ActivityOtpBinding

    private var code: String = ""
  private  var email: String = ""
  private  var from: String = ""
    lateinit var activity: Activity
   private lateinit var network: Network
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.activity = this;
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        network = Network()
        init()
    }

    @SuppressLint("SetTextI18n")
    fun init() {
        if (intent != null) {
            code = intent?.getStringExtra("otp").toString()
            email = intent?.getStringExtra("email").toString()
            from = intent?.getStringExtra("from").toString()

            binding.tvOtp.text = "Otp has been send to $email Please verify"
        }
        val edit = arrayOf(binding.edt1, binding.edt2, binding.edt3, binding.edt4)

        binding.edt1.addTextChangedListener(GenericTextWatcher(edit, 0))
        binding.edt2.addTextChangedListener(GenericTextWatcher(edit, 1))
        binding.edt3.addTextChangedListener(GenericTextWatcher(edit, 2))
        binding.edt4.addTextChangedListener(GenericTextWatcher(edit, 3))


        binding.btnVerify.setOnClickListener { view ->
            // startActivity(new Intent(this,SetPasswordActivity.class));
            val OTP = binding.edt1.text.toString().trim() +
                    binding.edt2.text.toString().trim() +
                    binding.edt3.text.toString().trim() +
                    binding.edt4.text.toString().trim()
            if (OTP.length < 4) {
                Toast.makeText(
                    this@OtpActivity,
                    "Please Enter 4 Digit Valid Otp",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            verify(OTP)
        }
        binding.btnSendAgain.setOnClickListener { view -> resend() }
    }

    private fun verify(otp: String) {

        if (otp == code) {
            val map = HashMap<String, Any>()
            map["email"] = email

            network.post(true,activity,
                Constants.BASE_URL + Constants.EMAIL_VERIFY,
                "", map
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
                                MethodClass.hasError(this, error)
                            } else {
                                StyleableToast.makeText(
                                    this@OtpActivity,
                                    json.getString("success"),
                                    Toast.LENGTH_SHORT,
                                    R.style.mytoast
                                ).show()


                                if (from == "register") {
                                    startActivity(
                                        Intent(
                                            this@OtpActivity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                } else if (from == "forgot") {
                                    val intent = Intent(
                                        this@OtpActivity,
                                        SetPasswordActivity::class.java
                                    )
                                    intent.putExtra("email", email)
                                    startActivity(intent)
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

    private fun resend() {
        val map = java.util.HashMap<String, Any>()
        map["email"] = email

        network.post(true,
            activity,
            Constants.BASE_URL + Constants.RESEND_OTP,
            "",
            map
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
                            MethodClass.hasError(this, error)
                        } else {


                            val json = Gson()
                            val registerResponse: ResendOtpResponse = json.fromJson(
                                res.toString(),
                                ResendOtpResponse::class.java
                            )
                            Toast.makeText(
                                this@OtpActivity,
                                "" + registerResponse.getSuccess(),
                                Toast.LENGTH_SHORT
                            ).show()
                            this.code = registerResponse.getOtp()

                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "login: $url " + e.message)
                    }


                }


            }


        }

    }
}