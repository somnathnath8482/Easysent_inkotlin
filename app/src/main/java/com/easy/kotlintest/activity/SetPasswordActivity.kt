package com.easy.kotlintest.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Network
import com.easy.kotlintest.R
import com.easy.kotlintest.databinding.ActivitySetPasswordBinding
import io.github.muddz.styleabletoast.StyleableToast
import org.json.JSONException
import org.json.JSONObject

class SetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetPasswordBinding
    private lateinit var email: String
    private lateinit var network: Network
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity  =this
        binding = ActivitySetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        network = Network();
        init()
    }

    private fun init() {
        email = intent?.getStringExtra("email") ?: ""

        binding.btnSubmit.setOnClickListener { view ->
            if (validate()) {
                val map =
                    HashMap<String, Any>()
                map["password"] = binding.password.text.toString().trim()
                map["email"] = email
                submit(map)
            }
        }

    }


    private fun submit(map: java.util.HashMap<String, Any>) {

        network.post(true,activity,
            Constants.BASE_URL + Constants.SET_PASSWORD,
            "",
            map

        ) { url, code, res ->
            if (code == "200")
                try {
                    if (res != null) {
                        val json = JSONObject(res)

                        StyleableToast.makeText(
                            applicationContext,
                            json.getString("success"),
                            Toast.LENGTH_SHORT,
                            R.style.mytoast
                        ).show()
                        startActivity(Intent(this@SetPasswordActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@SetPasswordActivity,
                        "Something Went Wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


    }

    private fun validate(): Boolean {
        if (!MethodClass.checkEditText(binding.password)) {
            binding.password.error = "Please Enter Password"
            binding.password.requestFocus()
            return false
        } else if (binding.password.text.trim().length < 8) {
            binding.password.error = "Password Must be 8 Character Long"
            binding.password.requestFocus()
            return false
        }
        if (!MethodClass.checkEditText(binding.conformPassword)) {
            binding.conformPassword.error = "Please  Re Enter Your Password"
            binding.conformPassword.requestFocus()
            return false
        } else if (!binding.password.text.toString().trim()
                .equals(binding.conformPassword.text.toString().trim())
        ) {
            binding.conformPassword.error = "Conform Password Must Same as New Password"
            binding.conformPassword.requestFocus()
            return false
        }
        return true
    }

}