package com.easy.kotlintest.activity.Message

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
import com.easy.kotlintest.Helper.CustomProgressbar
import com.easy.kotlintest.Helper.FileHandel.Onselect
import com.easy.kotlintest.Helper.FileHandel.PickFile
import com.easy.kotlintest.Helper.ImageGetter
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Messages.Message_View_Model
import com.easy.kotlintest.Room.Thread.Thread_ViewModel
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.Workers.SendMultipartMessageWorker
import com.easy.kotlintest.Workers.SendTextMessageWorker
import com.easy.kotlintest.adapter.Message.MessageNewAdapter
import com.easy.kotlintest.databinding.ActivityMessageBinding
import com.easy.kotlintest.databinding.AttachmentLayoutBinding
import com.easy.kotlintest.databinding.MainToolbarBinding
import com.easy.kotlintest.socket.LiveMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    lateinit var encripter:Encripter
    val sender: String = PrefUtill.getUser()?.user?.id ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this@MessageActivity
        binding = ActivityMessageBinding.inflate(layoutInflater)
        if (intent != null) {
            reciver = intent.getStringExtra("reciver") ?: ""
            encripter= Encripter(sender)
           thread =  com.easy.kotlintest.Helper.MethodClass.getThread(sender,reciver).toString()
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
            binding.recycler,
            this@MessageActivity
        )


        livedata?.observe(this) { it ->
            launch {
                adapter.submitData(it);
                setContentView(binding.root)
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        total = adapter.snapshot().size
                        Constants.ACTIVE = thread
                        thread_viewModel.updateUnread(thread)
                        binding.recycler.smoothScrollToPosition(total-1)
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


        initFile()

        onclick()
    }

    private fun initFile() {
        if (pickFile != null) pickFile.setOnselect(object : Onselect {
            override fun onSelect(vararg strings: String?) {
                val path = if (strings != null) strings[1] else null
                val ty = if (strings != null) strings[0] else null
                if (ty != null && path != null) {
                    val file = File(path)
                    if (file.exists()) {
                        val name: String = ty
                        val lastIndexOf = name.lastIndexOf(".")
                        var type = ""
                        type = if (lastIndexOf == -1) {
                            Toast.makeText(activity, "Failed to select file", Toast.LENGTH_SHORT)
                                .show()
                            return
                        } else {
                            name.substring(lastIndexOf)
                        }
                        if (type.equals(".png", ignoreCase = true) || type.equals(
                                ".JPEG",
                                ignoreCase = true
                            ) || type.equals(".JPG", ignoreCase = true)
                        ) {
                            filePath = MethodClass.getRightAngleImage(path)
                            //binding.ivAttachment.setImageBitmap(BitmapFactory.decodeFile(filePath));
                            ImageGetter(binding.ivAttachment).execute(file)
                            binding.idTvAttachment.text = file.name
                            fileType = "I"
                            binding.ivAttachment.visibility = View.VISIBLE
                            binding.layDoc.visibility = View.GONE
                        } else if (type.equals(".PDF", ignoreCase = true)) {
                            binding.ivDoc.setImageDrawable(resources.getDrawable(R.drawable.ic_pdf))
                            binding.idTvAttachment.text = file.name
                            fileType = "P"
                            binding.ivAttachment.visibility = View.GONE
                            binding.layDoc.visibility = View.VISIBLE
                        } else if (type.equals(".mp4", ignoreCase = true)) {
                            binding.ivAttachment.setImageDrawable(resources.getDrawable(R.drawable.ic_video))
                            Thread {
                                if (file.exists()) {
                                    val futureTarget =
                                        Glide.with(activity).asBitmap().override(600, 600)
                                            .load(file.absolutePath).submit()
                                    try {
                                        val bi = futureTarget.get()
                                        handler.post(Runnable {
                                            Glide.with(activity).load(bi)
                                                .into(
                                                    binding.ivDoc
                                                )
                                        })
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }.start()
                            binding.idTvAttachment.text = file.name
                            fileType = "V"
                            binding.ivAttachment.visibility = View.GONE
                            binding.layDoc.visibility = View.VISIBLE
                        } else {
                            binding.ivDoc.setImageDrawable(resources.getDrawable(R.drawable.ic_files))
                            binding.idTvAttachment.text = file.name
                            fileType = "D"
                            binding.ivAttachment.visibility = View.GONE
                            binding.layDoc.visibility = View.VISIBLE
                        }
                        val transition: Transition = Slide(Gravity.START)
                        transition.duration = 10
                        transition.addTarget(binding.layAttach)
                        filePath = path
                        TransitionManager.beginDelayedTransition(
                            binding.layAttach.parent as ViewGroup,
                            transition
                        )
                        binding.layAttach.visibility = View.VISIBLE
                        binding.dismissReplay.performClick()
                    }
                }
            }
        })

    }

    private fun onclick() {
        binding.dismiss.setOnClickListener { view12 ->
            val transition: Transition =
                Slide(Gravity.START)
            transition.duration = 10
            transition.addTarget(binding.layAttach)
            filePath = ""
            fileType = ""
            forward = ""
            TransitionManager.beginDelayedTransition(
                binding.layAttach.parent as ViewGroup,
                transition
            )
            binding.layAttach.visibility = View.GONE
            binding.ivAttachment.visibility = View.GONE
            binding.layDoc.visibility = View.GONE
        }
        binding.dismissReplay.setOnClickListener { view12 ->
            val transition: Transition = Slide(Gravity.START)
            transition.duration = 10
            transition.addTarget(binding.layReplay)
            forward = ""
            TransitionManager.beginDelayedTransition(
                binding.layReplay.parent as ViewGroup,
                transition
            )
            binding.layReplay.visibility = View.GONE
            binding.layReplayDoc.visibility = View.GONE
        }

        binding.toolbar.back.setOnClickListener {  onBackPressedDispatcher.onBackPressed() }



        val senderName: String = PrefUtill.getUser()?.user?.name ?: ""
        val email: String = PrefUtill.getUser()?.user?.email ?: ""



        binding.btnSend.setOnClickListener {
            val mid = Calendar.getInstance().timeInMillis.toString() + ""
            if (filePath == "" && binding.textSend.text.toString().trim().equals("")) {
                Toast.makeText(activity, "You Can't Send Empty Message", Toast.LENGTH_SHORT).show()
            } else {

                val message: String = encripter.encrypt(binding.textSend.text.toString().trim())




                if (fileType == "T") {
                    sendMessage(sender, senderName, reciver, message, activity, email, mid)
                } else if (fileType == "I" && filePath != "") {

                    if (fileType.equals("I", ignoreCase = true)) {
                        CustomProgressbar.showProgressBar(activity, false)
                        MethodClass.cashattachmentImage2(
                            File(filePath),
                            mid,
                            handler,
                            activity,
                            object : AllInterFace() {
                                override fun IsClicked(s: String) {
                                    super.IsClicked(s)
                                    if (s == null) {
                                        binding.layAttach.visibility = View.GONE
                                        filePath = ""
                                        fileType = ""
                                        binding.textSend.setText("")
                                        Toast.makeText(
                                            activity,
                                            "Unable to sent attachment",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        filePath = s
                                        //Toast.makeText(activity,filePath, Toast.LENGTH_SHORT).show();
                                        sendMessage(
                                            sender,
                                            senderName,
                                            reciver,
                                            message,
                                            activity,
                                            email,
                                            mid
                                        )
                                    }
                                }
                            })
                        CustomProgressbar.hideProgressBar()

                    }


                } else if (filePath != "") {

                    CustomProgressbar.showProgressBar(activity, false)
                    MethodClass.cashattachmentFILE2(
                        File(filePath),
                        mid,
                        handler,
                        activity,
                        object : AllInterFace() {
                            override fun IsClicked(s: String) {
                                super.IsClicked(s)
                                if (s == null) {
                                    binding.layAttach.visibility = View.GONE
                                    filePath = ""
                                    fileType = ""
                                    binding.textSend.setText("")
                                    Toast.makeText(
                                        activity,
                                        "Unable to sent attachment",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    filePath = s
                                    //Toast.makeText(activity,filePath, Toast.LENGTH_SHORT).show();
                                    sendMessage(
                                        sender,
                                        senderName,
                                        reciver,
                                        message,
                                        activity,
                                        email,
                                        mid
                                    )
                                }
                            }
                        })
                    CustomProgressbar.hideProgressBar()

                }

            }

        }

        binding.attach.setOnClickListener { view1 -> AddAttachMent() }
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
            map["is_1st"] = total == 0
            map["thread"] = thread
            map["sender_name"] = sender_name
            map["reciver"] = reciver
            map["message"] = message
            map["m_id"] = m_id
            map["type"] = fileType
            if (forward != "") {
                map["replay_of"] = forward
            }



            val utcFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = utcFormat.format(Date())
            map["date"] = date

            val workData: Data = Data.Builder()
                .putAll(map)
                .putString("multipart", filePath)
                .putString("key", "attachment")
                .build()

            val uploadWorkRequest = if (filePath != "" && fileType != "T") {
                OneTimeWorkRequestBuilder<SendMultipartMessageWorker>()
                    .setInputData(workData) // If you need to pass data to the worker
                    .addTag("MULTIPART-MESSAGE")
                    .build()
            } else {
                OneTimeWorkRequestBuilder<SendTextMessageWorker>()
                    .setInputData(workData) // If you need to pass data to the worker
                    .addTag("TEXT-MESSAGE")
                    .build()
            }


            val manager = WorkManager
                .getInstance(context)


            val chats = if (filePath != "" && fileType != "T") {
                Chats(
                    File(filePath).name + "",
                    sender,
                    sender_name,
                    date,
                    m_id,
                  thread,
                    encripter.decrepit(message),
                    fileType,
                    reciver,
                    "0",
                    uploadWorkRequest.id,
                    forward,
                    "0"
                )
            } else {
                Chats(
                    "",
                    sender,
                    sender_name,
                    date,
                    m_id,
                  thread,
                    encripter.decrepit(message),
                    "T",
                    reciver,
                    "0",
                    uploadWorkRequest.id,
                    forward,
                    "0"
                )
            }



          LiveMessage.sendMessage(chats,PrefUtill.getUser()?.user?.profilePic)
            //insert to rom before start the work
            message_view_model.insert(chats)
            thread_viewModel.updateLastSeen(
                encripter.decrepit(message),
                "0",
                "T",
               thread,
                date
            )

            //add to worker
            manager.enqueue(uploadWorkRequest)

            binding.textSend.setText("")
            binding.dismiss.performClick()
            binding.dismissReplay.performClick()
            fileType = "T"
            filePath = ""
            forward = ""
        }
    }


    private fun AddAttachMent() {
        val dialog = BottomSheetDialog(activity, R.style.SheetDialog)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.attachment_layout, null)
        val binding1: AttachmentLayoutBinding = AttachmentLayoutBinding.bind(dialogView)
        dialog.setContentView(dialogView)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        binding1.layImages.setOnClickListener {
            dialog.dismiss()
            pickFile.PickImage(false)
        }
        binding1.layPdf.setOnClickListener {
            dialog.dismiss()
            pickFile.PickPDF()
        }
        binding1.layDocs.setOnClickListener {
            dialog.dismiss()
            pickFile.PickDoc()
        }
        binding1.layVideo.setOnClickListener {
            dialog.dismiss()
            pickFile.Pickvideo()
        }
        binding1.layCaptureImage.setOnClickListener {
            pickFile.captureImage()
            dialog.dismiss()
        }
        binding1.layCaptureVideo.setOnClickListener { dialog.dismiss() }
    }

    override fun onDestroy() {
        super.onDestroy()
       /* mSocket.disconnect()
        mSocket.off("new message", object : Emitter.Listener {
            override fun call(vararg args: Any?) {
            }
        })*/
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}