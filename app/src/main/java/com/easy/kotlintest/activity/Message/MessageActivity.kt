package com.easy.kotlintest.activity.Message

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.easy.kotlintest.Encription.Encripter
import com.easy.kotlintest.Helper.FileHandel.PickFile
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Messages.Message_View_Model
import com.easy.kotlintest.Room.Thread.Thread_ViewModel
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.Workers.MultipartWorker
import com.easy.kotlintest.Workers.SendTextMessageWorker
import com.easy.kotlintest.adapter.Message.MessageNewAdapter
import com.easy.kotlintest.databinding.ActivityMessageBinding
import com.easy.kotlintest.databinding.MainToolbarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MessageActivity : AppCompatActivity(), CoroutineScope {
    private var thread: String = ""
    private var total: Int = 0
    private var filePath: String = ""
    private var fileType: String = "T"
    private var forward: String = ""
    private var livedata: LiveData<PagingData<Chats>>? = null
    private lateinit var reciver: String
    private lateinit var message_view_model: Message_View_Model
    private lateinit var thread_viewModel: Thread_ViewModel
    private lateinit var userVewModel: UserVewModel
    private lateinit var mainToolbarBinding: MainToolbarBinding
    private lateinit var binding: ActivityMessageBinding
    private lateinit var pickFile: PickFile
    private var handler = Handler(Looper.getMainLooper())
    lateinit var context: Context;
    lateinit var activity: Activity;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this@MessageActivity
        binding = ActivityMessageBinding.inflate(layoutInflater)
        if (intent != null) {
            reciver = intent.getStringExtra("reciver") ?: ""
        }

        userVewModel = ViewModelProviders.of(this)[UserVewModel::class.java]
        thread_viewModel = ViewModelProviders.of(this)[Thread_ViewModel::class.java]
        message_view_model = ViewModelProviders.of(this)[Message_View_Model::class.java]

        message_view_model.chatByPaged(reciver, PrefUtill.getUser()?.user?.id) {
            livedata = it
        }
        val adapter = MessageNewAdapter(
            object : DiffUtil.ItemCallback<Chats>() {
                override fun areItemsTheSame(oldItem: Chats, newItem: Chats): Boolean {
                    val boll = oldItem.id == newItem.id
                    //Log.e("TAG", "areItemsTheSame: $boll" )
                    return boll
                }


                override fun areContentsTheSame(oldItem: Chats, newItem: Chats): Boolean {
                    val boo = (oldItem.seen == newItem.seen && oldItem.deleted == newItem.deleted);
                    //Log.e("TAG", "areContentsTheSame: $boo" )
                    return boo
                }
            },
            Dispatchers.Main,
            this@MessageActivity,
            this,
            userVewModel,
            handler,
            message_view_model,
            binding.recycler
        )


        livedata?.observe(this) { it ->
            launch {
                adapter.submitData(it);
                setContentView(binding.root)
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        total = adapter.snapshot().size
                        thread = adapter.snapshot()[0]?.thread?:""
                        Constants.ACTIVE = thread
                        thread_viewModel.updateUnread(thread)
                    }
                })
            }

        }

        context = this

        pickFile = PickFile(this, this, handler)

        mainToolbarBinding = MainToolbarBinding.bind(binding.toolbar.root)
        mainToolbarBinding.back.setVisibility(View.VISIBLE)
        mainToolbarBinding.menu.setVisibility(View.GONE)
        mainToolbarBinding.menuMessage.setVisibility(View.VISIBLE)


        val linearLayoutManager = LinearLayoutManager(this@MessageActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = false
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.isNestedScrollingEnabled = false
        binding.recycler.setHasFixedSize(true)
        binding.recycler.adapter = adapter


        message_view_model.chatByPaged(reciver, PrefUtill.getUser()?.user?.id) {
            handler.post {
                it.observe(this) {
                    launch {
                        adapter.submitData(it);
                    }

                }
            }
        }


        userVewModel.selectUserLive(
            reciver
        ) { item ->
            handler.post(kotlinx.coroutines.Runnable {
                item?.observe(this@MessageActivity)
                { user ->
                    if (user != null) {
                        mainToolbarBinding.name.text = user.name
                        mainToolbarBinding.email.text = user.email

                        if (user.profilePic != null && !user?.profilePic.equals("null")) {
                            val url: String =
                                Constants.BASE_URL + "profile_image/" + user.profilePic
                            val file = File(Constants.CATCH_DIR_CASH + "/" + user.profilePic)
                            if (file.exists()) {
                                launch {
                                    Glide.with(context)
                                        .load(file)
                                        .into(mainToolbarBinding.profileImage)
                                }
                            } else {

                                launch {

                                    val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                                        .asBitmap()
                                        .override(100, 100)
                                        .load(url)
                                        .submit()

                                    try {

                                        val bim = futureTarget.get()

                                        Glide.with(context)
                                            .load(bim)
                                            .into(mainToolbarBinding.profileImage)

                                        MethodClass.CashImageInCatch(user.profilePic, bim)

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                            }
                        } else {
                            mainToolbarBinding.profileImage.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    context, MethodClass.getResId(
                                        user.name, Drawable::class.java
                                    )
                                )
                            )
                        }

                    }

                }
            })
        }


        binding.btnSend.setOnClickListener {
            val mid = Calendar.getInstance().timeInMillis.toString() + ""
            if (filePath == "" && binding.textSend.text.toString().trim().equals("")) {
                Toast.makeText(activity, "You Can't Send Empty Message", Toast.LENGTH_SHORT).show()
            } else {
                val sender: String = PrefUtill.getUser()?.user?.id?:""
                val senderName: String = PrefUtill.getUser()?.user?.name?:""
                val encripter = Encripter(sender)
                val message: String = encripter.encrypt(binding.textSend.text.toString().trim())
                val email: String = PrefUtill.getUser()?.user?.email?:""

                fileType  ="T"
                sendMessage(sender, senderName, reciver, message, activity, email, mid)

            }
        }
    }

            private fun sendMessage(
                sender: String,
                sender_name: String,
                reciver: String,
                message: String,
                context: Activity,
                email: String,
                m_id: String
            ) {
                launch {
                    val map = HashMap<String, Any>()
                    map["sender"] = sender
                    map["sender_name"] = sender_name
                    map["reciver"] = reciver
                    map["message"] = message
                    map["m_id"] = m_id
                    map["type"] = fileType
                    if (forward != "") {
                        map["replay_of"] = forward
                    }


                    val encripter = Encripter(sender)
                    val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    utcFormat.timeZone = TimeZone.getTimeZone("UTC")

                    val date = utcFormat.format(Date())

                    val chats = Chats(
                        "",
                        sender,
                        date,
                        m_id,
                        if (total == 0) m_id else thread,
                        encripter.decrepit(message),
                        "T",
                        reciver,
                        "0",
                        forward,
                        "0"
                    )
                    message_view_model.insert(chats)
                    thread_viewModel.updateLastSeen(
                        encripter.decrepit(message),
                        "0",
                        "T",
                        if (total == 0) m_id else thread,
                        date
                    )



                    val workData: Data = Data.Builder()
                        .putAll(map)
                        .build()
                    val uploadWorkRequest = OneTimeWorkRequestBuilder<SendTextMessageWorker>()
                        .setInputData(workData) // If you need to pass data to the worker
                        .build()

                    val manager = WorkManager
                        .getInstance(context)

                    manager.enqueue(uploadWorkRequest)
                    manager.getWorkInfoByIdLiveData(uploadWorkRequest.id).observe(this@MessageActivity
                    ) { value ->
                        val res = value?.outputData?.getString("res")
                        binding.textSend.setText("")
                        fileType=""
                        filePath=""
                        forward=""
                        try{
                            val obj = JSONObject(res?:"")
                            if (obj.has("error")){
                               Toast.makeText(context,"Failed to send",Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch(e:Exception){
                            e.printStackTrace()
                        }
                    }


                }
            }

            override val coroutineContext: CoroutineContext
            get() = Dispatchers.Main
        }