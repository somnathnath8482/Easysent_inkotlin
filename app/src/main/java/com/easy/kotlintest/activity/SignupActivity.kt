package com.easy.kotlintest.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.Constants.Home_URL
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Network
import com.easy.kotlintest.R
import com.easy.kotlintest.Response.Register.RegisterResponse
import com.easy.kotlintest.databinding.ActivitySignupBinding
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var network: Network
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        activity = this
        setContentView(binding.root)
        network = Network()
        init();
    }

    private fun init() {
        binding.btnRegister.setOnClickListener(View.OnClickListener {
            if (!binding.accept.isChecked) {
                StyleableToast.makeText(
                    this@SignupActivity,
                    "Please Read AND Accept The Terms & Condition",
                    R.style.mytoast
                ).show()
                return@OnClickListener
            }
            val map = HashMap<String, Any>()
            try {
                map["name"] = MethodClass.CheckEmpty(binding.edFillname)
                map["email"] = MethodClass.CheckEmpty(binding.email)
                map["phone"] = MethodClass.CheckEmpty(binding.phone)
                map["password"] = MethodClass.CheckEmpty(binding.edPassword)
                map["username"] = MethodClass.CheckEmpty(binding.edUsername)
                map["c_password"] = MethodClass.CheckEmpty(binding.edConPassword)
                if (!Validate()) {
                    return@OnClickListener
                }
            } catch (e: Exception) {
                val editText: EditText = binding.root.findViewById(e.message!!.toInt())
                editText.requestFocus()
                e.printStackTrace()
                return@OnClickListener
            }

            network.post(true,activity,
                Constants.BASE_URL + Constants.REGISTER_USER, "", map,
            ) { url, code, res ->
                if (code == "200") {
                    kotlin.run {
                        if (res != null) {
                            val json = Gson()
                            val registerResponse: RegisterResponse = json.fromJson(
                                res.toString(),
                                RegisterResponse::class.java
                            )
                            if (registerResponse != null) {
                                StyleableToast.makeText(
                                    applicationContext,
                                    registerResponse.getSuccess(),
                                    Toast.LENGTH_SHORT,
                                    R.style.mytoast
                                ).show()
                                val intent = Intent(applicationContext, OtpActivity::class.java)
                                intent.putExtra("otp", registerResponse.getOtp())
                                intent.putExtra("from", "register")
                                intent.putExtra("email", binding.email.text.toString().trim())
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

        })
        binding.btnLogin.setOnClickListener {
            startActivity(
                Intent(
                    this@SignupActivity,
                    LoginActivity::class.java
                )
            )
        }


        val ss = SpannableString(resources.getString(R.string.accepTc))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(Home_URL + "Terms")
                startActivity(i)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = resources.getColor(R.color.blue)
            }
        }
        ss.setSpan(clickableSpan, 10, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvAccep.text = ss
        binding.tvAccep.movementMethod = LinkMovementMethod.getInstance()

    }


    private fun Validate(): Boolean {
        if (!MethodClass.checkEditText(binding.email)) {
            binding.email.error = "Please Enter Email Id"
            binding.email.requestFocus()
            return false
        } else if (!MethodClass.isValidEmailId(binding.email.text.toString().trim())) {
            binding.email.error = "Please Enter A Valid Email Id"
            binding.email.requestFocus()
            return false
        }
        if (!MethodClass.checkEditText(binding.edUsername)) {
            binding.edUsername.error = "Please Enter Username"
            binding.edUsername.requestFocus()
            return false
        }
        if (!MethodClass.checkEditText(binding.edPassword)) {
            binding.edPassword.error = "Please Enter Password"
            binding.edPassword.requestFocus()
            return false
        } else if (binding.edPassword.text.trim().length < 8) {
            binding.edPassword.error = "Password Must be 8 Character Long"
            binding.edPassword.requestFocus()
            return false
        }
        if (!MethodClass.checkEditText(binding.edConPassword)) {
            binding.edConPassword.error = "Please  Re Enter Your Password"
            binding.edConPassword.requestFocus()
            return false
        } else if (!binding.edPassword.text.toString().trim()
                .equals(binding.edConPassword.text.toString().trim())
        ) {
            binding.edConPassword.error = "Conform Password Must Same as New Password"
            binding.edConPassword.requestFocus()
            return false
        }
        return true
    }
}