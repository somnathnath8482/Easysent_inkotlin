package com.easy.kotlintest.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.easy.kotlintest.Helper.FileHandel.PickFile
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Network
import com.easy.kotlintest.R
import com.easy.kotlintest.Response.Login.LoginResponse
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.Room.Users.Users
import com.easy.kotlintest.Workers.MultipartWorker
import com.easy.kotlintest.databinding.ActivityEditProfilrBinding
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import kotlin.coroutines.CoroutineContext

class EditProfilrActivity : AppCompatActivity(), CoroutineScope {

    private val handler: Handler = Handler(Looper.getMainLooper())
    lateinit var binding: ActivityEditProfilrBinding
    private var profilePicPath = ""
    private lateinit var pickFile: PickFile
    private lateinit var uuserviewModel: UserVewModel
    private lateinit var network: Network
    private lateinit var context: Context

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var handeler: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfilrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@EditProfilrActivity
        pickFile = PickFile(this@EditProfilrActivity, this, handeler)
        uuserviewModel = UserVewModel(application)
        network = Network()

        init()
    }

    private fun init() {


        uuserviewModel.selectUserLive(
            PrefUtill.getUser()?.user?.id
        ) { item ->
            handler.post(kotlinx.coroutines.Runnable {
                item?.observe(this@EditProfilrActivity, ::populate)
            })
        }


        binding.btnSubmit.setOnClickListener {
            try {
                val map = HashMap<String, Any>()
                map["name"] = MethodClass.CheckEmpty(binding.name)
                map["email"] = MethodClass.CheckEmpty(binding.email)
                map["phone"] = MethodClass.CheckEmpty(binding.phone)
                map["about"] = MethodClass.CheckEmpty(binding.about)
                if (binding.male.isChecked) {
                    map["gender"] = "M"
                } else if (binding.female.isChecked) {
                    map["gender"] = "F"
                } else if (binding.other.isChecked) {
                    map["gender"] = "N"
                } else {
                    Toast.makeText(context, "Please Select Gender", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                update(map)
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }



        pickFile.setOnselect { strings ->
            if (strings != null) {
                Log.e("PhotoPicker", "onSelect: " + strings[0] + " " + strings[1])
                val s = strings[1]
                profilePicPath = s
                val file = File(s)
                if (file.exists()) {
                    binding.ivProfile.setImageBitmap(BitmapFactory.decodeFile(s))
                }
            }
        }

        binding.ivProfile.setOnClickListener { view1 -> pickFile.PickImage(false) }
    }

    private fun update(map: HashMap<String, Any>) {

        if (profilePicPath.isEmpty()) {
            network.post(
                true,
                this@EditProfilrActivity,
                Constants.BASE_URL + Constants.EDIT_PROFILE,
                "",
                map
            ) { _, _, res ->
                profilePicPath = ""
                val gson = Gson()
                val loginResponse: LoginResponse =
                    gson.fromJson(res.toString(), LoginResponse::class.java)
                StyleableToast.makeText(
                    context,
                    loginResponse.message,
                    Toast.LENGTH_SHORT,
                    R.style.mytoast
                ).show()

                PrefUtill.setUser(loginResponse)
                uuserviewModel.update(
                    loginResponse.user.phone,
                    loginResponse.user.name,
                    loginResponse.user.profilePic,
                    loginResponse.user.gender,
                    loginResponse.user.about,
                    loginResponse.user.isVerifyed,
                    loginResponse.user.email,
                    loginResponse.user.token
                )


            }
        } else {
            val workData: Data = Data.Builder().putAll(map)
                .putString("url", Constants.BASE_URL + Constants.EDIT_PROFILE)
                .putString("multipart", profilePicPath)
                .putString("key", "img")
                .build()
            val uploadWorkRequest = OneTimeWorkRequestBuilder<MultipartWorker>()
                .setInputData(workData) // If you need to pass data to the worker
                .build()

            val manager = WorkManager
                .getInstance(context)

            manager.enqueue(uploadWorkRequest)

            manager.getWorkInfoByIdLiveData(uploadWorkRequest.id)
                .observe(this) { value ->
                    val res = value?.outputData?.getString("res")
                    if (res != null && res != "" && value.state.name=="SUCCEEDED") {
                        profilePicPath = ""
                        val gson = Gson()
                        val loginResponse: LoginResponse =
                            gson.fromJson(res.toString(), LoginResponse::class.java)
                        StyleableToast.makeText(
                            context,
                            loginResponse.message,
                            Toast.LENGTH_SHORT,
                            R.style.mytoast
                        ).show()

                        PrefUtill.setUser(loginResponse)
                        uuserviewModel.update(
                            loginResponse.user.phone,
                            loginResponse.user.name,
                            loginResponse.user.profilePic,
                            loginResponse.user.gender,
                            loginResponse.user.about,
                            loginResponse.user.isVerifyed,
                            loginResponse.user.email,
                            loginResponse.user.token
                        )

                    }
                }


        }


    }

    private fun populate(user: Users) {
        binding.name.setText(user.name)
        binding.tvName.text = user.name
        binding.tvEmail.text = user.email
        binding.email.setText(user.email)

        binding.phone.setText(user.phone)
        binding.about.setText(user.about)

        if (user.gender.equals("M")) {
            binding.male.isChecked = true
        } else if (user.gender.equals("F")) {
            binding.female.isChecked = true
        } else if (user.gender.equals("N")) {
            binding.other.isChecked = true
        }


        Glide.with(context).load(Constants.BASE_URL + "profile_image/" + user.profilePic)
            .addListener(object : RequestListener<Drawable?> {
                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.ivProfile.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            MethodClass.getResId(
                                user.name,
                                Drawable::class.java
                            )
                        )
                    )
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.ivProfile.setImageDrawable(resource)
                    return false
                }
            }).into(binding.ivProfile)


    }


}