package com.easy.kotlintest.adapter.Message

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestListener
import com.easy.kotlintest.BuildConfig
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Helper.SaveWithProgress
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Messages.Message_View_Model
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.databinding.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.CoroutineContext


class MessageNewAdapter(
    diffCallback: DiffUtil.ItemCallback<Chats>,
    mainDispatcher: CoroutineDispatcher,
    var activity: Activity,
    var context: Context,
    var userVewModel: UserVewModel,
    var handler: Handler,
    var message_view_model: Message_View_Model,
    var recycler: RecyclerView,
    var owner: LifecycleOwner,
    var myId: String = PrefUtill.getUser()?.user?.id ?: "",
    private val manager: WorkManager = WorkManager.getInstance(context)
) : PagingDataAdapter<Chats, RecyclerView.ViewHolder>(diffCallback, mainDispatcher),
    CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item?.also {
            when (viewholder) {
                is LeftTextHolder -> {
                    viewholder.binding.sender.visibility = View.GONE

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()
                }
                is RightTextHolder -> {
                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()

                    status(item, viewholder.binding.status)
                }
                is LeftTextReplayHolder -> {
                    viewholder.binding.sender.visibility = View.GONE

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()
                }
                is RightTextReplayHolder -> {

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()

                    status(item, viewholder.binding.status)
                }
                is LeftAttachmentHolder -> {
                    viewholder.binding.sender.visibility = View.GONE

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()


                    when (item.type) {
                        "T" -> {

                        }
                        "I" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentImage(
                                item,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivDownloadProgress
                            )
                        }
                        "V" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentVideo(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        "P" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position,
                                viewholder.binding.fileType
                            )
                        }
                        else -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position ,
                                viewholder.binding.fileType
                            )
                        }
                    }

                }
                is RightAttachmentHolder -> {
                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()

                    status(item, viewholder.binding.status)

                    when (item.type) {
                        "T" -> {

                        }
                        "I" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentImage(
                                item,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivDownloadProgress
                            )
                        }
                        "V" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentVideo(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        "P" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position,
                                viewholder.binding.fileType
                            )
                        }
                        else -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position,
                                viewholder.binding.fileType
                            )
                        }
                    }
                }
                is LeftAllHolder -> {
                    viewholder.binding.sender.visibility = View.GONE

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()

                    when (item.type) {
                        "T" -> {
                        }
                        "I" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentImage(
                                item,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivDownloadProgress
                            )
                        }
                        "V" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentVideo(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        "P" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position,
                                viewholder.binding.fileType
                            )
                        }
                        else -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position,
                                viewholder.binding.fileType
                            )
                        }
                    }
                }
                is RightAllHolder -> {
                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()
                    status(item, viewholder.binding.status)

                    when (item.type) {
                        "T" -> {

                        }
                        "I" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentImage(
                                item,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivDownloadProgress
                            )
                        }
                        "V" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            attachmentVideo(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.btnPlay,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        "P" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position,
                                viewholder.binding.fileType
                            )
                        }
                        "D" -> {
                            if (item.message == null || item.message?.length == 0) {
                                viewholder.binding.message.visibility = View.GONE
                            }
                            viewholder.binding.btnPlay.visibility = View.GONE
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position,
                                viewholder.binding.fileType
                            )
                        }
                    }
                }
            }

        }


    }

    private fun status(item: Chats, status: ImageView) {

        if (item.seen.equals("2")) {
            status.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_double_check
                )
            )
            status.colorFilter = null
            status.setColorFilter(ContextCompat.getColor(context, R.color.white))
        } else if (item.seen.equals("1")) {
            status.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_check))
            status.colorFilter = null
            status.setColorFilter(ContextCompat.getColor(context, R.color.white))
        } else if (item.seen.equals("0")) {
            status.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_clock))
            status.colorFilter = null
            status.setColorFilter(ContextCompat.getColor(context, R.color.thim_color))
        } else {
            status.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_double_check
                )
            )
            status.colorFilter = null
            status.setColorFilter(ContextCompat.getColor(context, R.color.thim_color))
        }


    }


    private fun attachmentImage(
        item: Chats,
        imageview: ImageView,
        btnPlay: ImageView,
        ivDownloadProgress: ProgressBar
    ) {
        if (item.attachment != null && item.attachment != "null") {
            val file = File(Constants.CATCH_DIR_Memory + "/" + item.attachment)
            if (file.exists()) {
                /*    Glide.with(context)
                        .asBitmap()
                        .override(500, 300)
                        .load(file)
                        .into(imageview)*/

                MethodClass.GetFileBitmap(
                    file.absolutePath,
                    imageview,
                    context
                ).execute()
            } else {
                launch {
                    Glide.with(context)
                        .asBitmap()
                        .override(500, 500)
                        .load(Uri.parse(Constants.BASE_URL + "Attachment/" + item.attachment))
                        .addListener(object : RequestListener<Bitmap?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Bitmap?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Bitmap?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                try {
                                    imageview.setImageBitmap(resource)
                                    MethodClass.CashImageInMemoryOriginalQuality(
                                        item.attachment,
                                        resource
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()

                                }
                                return false
                            }

                        })
                        .submit()
                }
            }
            imageview.visibility = View.VISIBLE
            btnPlay.visibility = View.GONE
            imageview.setOnClickListener {
                MethodClass.showFullScreen(activity, handler, item.thread, item.id)
            }
        }

        if (item.workId != null && item.seen.equals("0")) {
            btnPlay.visibility = View.GONE
            ivDownloadProgress.visibility = View.VISIBLE
            ivDownloadProgress.max  =100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner ) { value ->
                val res = value?.outputData?.getString("res")
                val  progress = value?.progress?.getInt("progress",0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        btnPlay.visibility = View.GONE
                        Log.e("progress", "progress: $progress")
                        ivDownloadProgress.setProgress( progress?:0,true)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun attachmentDoc(
        item: Chats,
        download: ImageView,
        iv_doc: ImageView,
        ivDownloadProgress: ProgressBar,
        position: Int,
        fileType: TextView
    ) {
        Glide.with(context).load(AppCompatResources.getDrawable(context, R.drawable.ic_files))
            .override(300, 300).into(iv_doc)
        iv_doc.visibility = View.VISIBLE
        fileType.text = item.getFileType()
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {
                download.visibility = View.GONE
                ivDownloadProgress.visibility = View.GONE


                val path =
                    FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )
                val pdfOpenintent = Intent(Intent.ACTION_VIEW)
                pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                pdfOpenintent.data = path
                //pdfOpenintent.data = path
                iv_doc.setOnClickListener {
                    context.startActivity(pdfOpenintent)

                }


            } else {
                download.visibility = View.VISIBLE

                download.setOnClickListener {
                    downloadfile(
                        item,
                        download,
                        ivDownloadProgress,
                        position
                    )
                }

            }
        }
        if (item.workId != null && item.seen.equals("0")) {

            ivDownloadProgress.visibility = View.VISIBLE
            ivDownloadProgress.max  =100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner ) { value ->
                val res = value?.outputData?.getString("res")
                val  progress = value?.progress?.getInt("progress",0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Log.e("progress", "progress: $progress")
                        ivDownloadProgress.setProgress( progress?:0,true)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun attachmentPDF(
        item: Chats,
        download: ImageView,
        iv_doc: ImageView,
        ivDownloadProgress: ProgressBar,
        position: Int,
        fileType: TextView
    ) {

        iv_doc.visibility = View.VISIBLE
        fileType.text = item.getFileType()
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {

                val path =
                    FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )

                val mc = com.easy.kotlintest.Helper.MethodClass()
                mc.getThumbnail(context.contentResolver, path) {
                    handler.post(Runnable {
                        Glide.with(context).load(it)
                            .override(600, 500).into(iv_doc)
                    })

                }
                val pdfOpenintent = Intent(Intent.ACTION_VIEW)
                pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                pdfOpenintent.setDataAndType(path, "application/pdf")
                //pdfOpenintent.data = path
                iv_doc.setOnClickListener {
                    context.startActivity(pdfOpenintent)

                }
                download.visibility = View.GONE
            } else {
                download.visibility = View.VISIBLE
                download.setOnClickListener {
                    downloadfile(
                        item,
                        download,
                        ivDownloadProgress,
                        position
                    )
                }
            }
        }


        if (item.workId != null && item.seen.equals("0")) {
            ivDownloadProgress.visibility = View.VISIBLE
            ivDownloadProgress.max  =100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner ) { value ->
                val res = value?.outputData?.getString("res")
                val  progress = value?.progress?.getInt("progress",0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Log.e("progress", "progress: $progress")
                        ivDownloadProgress.setProgress( progress?:0,true)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun attachmentVideo(
        item: Chats,
        download: ImageView,
        play: ImageView,
        attachment: ImageView,
        ivDownloadProgress: ProgressBar,
        position: Int
    ) {
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            attachment.visibility = View.VISIBLE
            if (file.exists()) {
                val cashFile = File(Constants.CATCH_DIR_CASH + "/" + item.attachment)
                if (cashFile.exists()) {

                    Glide.with(context)
                        .load(cashFile)
                        .override(400, 400)
                        .into(attachment)
                    play.visibility = View.VISIBLE
                    download.visibility = View.GONE

                } else {
                    Thread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                            .asBitmap()
                            .override(600, 600)
                            .load(file.absolutePath)
                            .submit()
                        try {
                            val bitmap = futureTarget.get()
                            handler.post {
                                Glide.with(context)
                                    .load(bitmap)
                                    .override(600, 600)
                                    .into(attachment)

                                play.visibility = View.VISIBLE
                                download.visibility = View.GONE

                            }
                            MethodClass.CashImageInCatch(
                                file.name.replace("mp4", "png"),
                                bitmap
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                }
            } else {

                val cashFile = File(Constants.CATCH_DIR_CASH + "/" + item.attachment)
                if (cashFile.exists()) {
                    Glide.with(context)
                        .load(cashFile)
                        .override(400, 400)
                        .into(attachment)
                    play.visibility = View.GONE
                    download.visibility = View.VISIBLE
                    download.setOnClickListener {
                        downloadfile(
                            item,
                            download,
                            ivDownloadProgress,
                            position
                        )
                    }
                } else {


                    Thread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                            .asBitmap()
                            .override(600, 600)
                            .load("${Constants.Attachment_URL}/${item.attachment}")
                            .submit()
                        try {
                            val bitmap = futureTarget.get()
                            handler.post {
                                Glide.with(context)
                                    .load(bitmap)
                                    .override(400, 400)
                                    .into(attachment)

                                play.visibility = View.GONE
                                download.visibility = View.VISIBLE
                                download.setOnClickListener {
                                    downloadfile(
                                        item,
                                        download,
                                        ivDownloadProgress,
                                        position
                                    )
                                }

                            }
                            MethodClass.CashImageInCatch(
                                file.name.replace("mp4", "png"),
                                bitmap
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                }

            }

            attachment.setOnClickListener {
                MethodClass.showFullScreen(activity, handler, item.thread, item.id)
            }
        }

        if (item.workId != null && item.seen.equals("0")) {
            play.visibility = View.GONE
            ivDownloadProgress.visibility = View.VISIBLE
            ivDownloadProgress.max  =100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner ) { value ->
                val res = value?.outputData?.getString("res")
                val  progress = value?.progress?.getInt("progress",0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        play.visibility = View.GONE
                        Log.e("progress", "progress: $progress")
                            ivDownloadProgress.setProgress( progress?:0,true)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun downloadfile(
        item: Chats,
        iv_download: ImageView,
        iv_download_progress: ProgressBar,
        position: Int
    ) {
        iv_download.visibility = View.GONE
        iv_download_progress.visibility = View.VISIBLE

        val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
        val saveWithProgress = SaveWithProgress(
            "${Constants.Attachment_URL}/${item.attachment}",
            file,
            iv_download_progress,
            handler
        )
        saveWithProgress.save()
        saveWithProgress.setAllInterface(object : AllInterFace() {
            override fun isClicked(is_success: Boolean) {
                super.isClicked(is_success)
                if (is_success) {
                    notifyItemChanged(position)
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when {
            item != null -> {
                if (item.sender.equals(myId, ignoreCase = true)) {
                    when (item.type) {
                        "T" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_right_text_replay

                        } else {
                            R.layout.chat_right_text
                        }
                        "I", "V", "D" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_right_attachment
                        } else {
                            R.layout.chat_right_all
                        }

                        else -> {
                            R.layout.chat_right_all
                        }
                    }
                } else {
                    when (item.type) {
                        "T" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_left_text_replay

                        } else {
                            R.layout.chat_left_text
                        }
                        "I", "V", "D" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_left_attachment
                        } else {
                            R.layout.chat_left_all
                        }
                        else -> R.layout.chat_left_all
                    }
                }
            }
            else -> R.layout.chat_shimmer
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {


        val view = LayoutInflater.from(activity).inflate(viewType, parent, false)


        return when (viewType) {
            R.layout.chat_shimmer -> ShimmerHolder(view)

            R.layout.chat_left_all -> LeftAllHolder(view)
            R.layout.chat_left_text -> LeftTextHolder(view)
            R.layout.chat_left_attachment -> LeftAttachmentHolder(view)
            R.layout.chat_left_text_replay -> LeftTextReplayHolder(view)

            R.layout.chat_right_all -> RightAllHolder(view)
            R.layout.chat_right_text -> RightTextHolder(view)
            R.layout.chat_right_attachment -> RightAttachmentHolder(view)
            R.layout.chat_right_text_replay -> RightTextReplayHolder(view)
            else -> ShimmerHolder(view)
        }


    }


    @SuppressLint("SetTextI18n")
    class LeftAllHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*  val mainLay: ConstraintLayout? = itemView.findViewById(R.id.main_lay)
          val message: TextView? = itemView.findViewById(R.id.message)
          val time: TextView? = itemView.findViewById(R.id.time)
          val sender: TextView? = itemView.findViewById(R.id.sender)
          val ivAttachmentTitle: TextView? = itemView.findViewById(R.id.iv_attachment_title)
          val layDoc: LinearLayout? = itemView.findViewById(R.id.lay_doc)
          val layAttachment: RelativeLayout? = itemView.findViewById(R.id.lay_attachment)
          val ivDownload: ImageView? = itemView.findViewById(R.id.iv_download)
          val ivDoc: ImageView? = itemView.findViewById(R.id.iv_doc)
          val btnPlay: ImageView? = itemView.findViewById(R.id.btn_play)
          val ivAttachment: ImageView? = itemView.findViewById(R.id.iv_attachment)
          val ivDownloadProgress: ProgressBar? = itemView.findViewById(R.id.iv_download_progress)
          val status: ImageView? = itemView.findViewById(R.id.status)
          val replaySender: TextView? = itemView.findViewById(R.id.replay_sender)
          val replayText: TextView? = itemView.findViewById(R.id.replay_text)
          val ivReplayAttachmentTitle: TextView? =
              itemView.findViewById(R.id.iv_replay_attachment_title)
          val layReplay: LinearLayout? = itemView.findViewById(R.id.lay_replay)
          val layReplayDoc: LinearLayout? = itemView.findViewById(R.id.lay_replay_doc)
          val ivReplayDoc: ImageView? = itemView.findViewById(R.id.iv_replay_doc)*/


        val binding = ChatLeftAllBinding.bind(itemView)
    }

    class LeftAttachmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatLeftAttachmentBinding.bind(itemView)
    }

    class LeftTextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ChatLeftTextBinding.bind(itemView)
    }

    class LeftTextReplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatLeftTextReplayBinding.bind(itemView)
    }

    class RightAllHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatRightAllBinding.bind(itemView)
    }

    class RightAttachmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatRightAttachmentBinding.bind(itemView)
    }

    class RightTextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatRightTextBinding.bind(itemView)
    }

    class RightTextReplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChatRightTextReplayBinding.bind(itemView)
    }

    class ShimmerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}