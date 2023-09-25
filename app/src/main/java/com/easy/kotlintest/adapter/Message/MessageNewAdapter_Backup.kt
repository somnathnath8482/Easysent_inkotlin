package com.easy.kotlintest.adapter.Message

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.easy.kotlintest.BuildConfig
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Helper.SaveWithProgress
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Networking.Helper.Constants.*
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.Networking.Interface.OnMenuItemClick
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Messages.Message_View_Model
import com.easy.kotlintest.Room.Users.UserVewModel
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File


class MessageNewAdapter_Backup(
    diffCallback: DiffUtil.ItemCallback<Chats>,
    mainDispatcher: CoroutineDispatcher,
    var activity: Activity,
    var context: Context,
    var userVewModel: UserVewModel,
    var handler: Handler,
    var message_view_model: Message_View_Model,
    var recycler: RecyclerView,
) : PagingDataAdapter<Chats, MessageNewAdapter_Backup.ViewHolder>(diffCallback, mainDispatcher) {
    private var my_id = PrefUtill.getUser()?.getUser()?.getId() ?: ""


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val time = arrayOf("")
        val message = arrayOf("")



        if (item != null) {
            Thread {
                time[0] = MethodClass.changeDateFormat2(item.createdAt)

                message[0] = item.message ?: ""


                handler?.post {
                    try {
                        holder.message.text = message[0]
                        holder.time.text = time[0]

                        if (!item.type.equals("T", ignoreCase = true) && !item.type.equals(
                                "I",
                                ignoreCase = true
                            )
                        ) {
                            holder.ivDownload.setOnClickListener { view ->
                                Download_file(
                                    item,
                                    holder.ivDownload,
                                    holder.ivDownloadProgress,
                                    position
                                )
                            }
                        }

                        holder.itemView.setOnLongClickListener { view ->
                            showPopupMenu(holder.mainLay, item)
                            true
                        }

                        if (!item.type.equals("T", ignoreCase = true) &&
                            !item.type.equals("I", ignoreCase = true) &&
                            !item.type.equals("V", ignoreCase = true)
                        ) {
                            holder.layDoc.setOnClickListener { view ->
                                val file = File(CATCH_DIR2 + "/" + item.attachment)
                                if (file.exists()) {
                                    val path = FileProvider.getUriForFile(
                                        context,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        file
                                    )
                                    val pdfOpenintent = Intent(Intent.ACTION_VIEW)
                                    pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    pdfOpenintent.data = path
                                    try {
                                        context.startActivity(pdfOpenintent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    holder.ivDownload.performClick()
                                }
                            }
                        }

                        when {
                            item.type.equals("T", ignoreCase = true) -> {
                                // TODO: Handle type T
                            }
                            item.type.equals("I", ignoreCase = true) -> {
                                attachmentImage(item, holder)
                            }
                            item.type.equals("V", ignoreCase = true) -> {
                                AttachmentVideo(item, holder)
                            }
                            item.type.equals("P", ignoreCase = true) -> {
                                AttachmentPDF(item, holder)
                            }
                            else -> {
                                AttachmentDoc(item, holder)
                            }
                        }

                        if (!item.sender.equals(my_id)) {

                            userVewModel?.selectUser(item.sender) {
                                holder.sender.text = it.name
                            }

                        } else {
                            when (item.seen) {
                                "2" -> {
                                    holder.status.setImageResource(R.drawable.ic_double_check)
                                    holder.status.clearColorFilter()
                                    holder.status.setColorFilter(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.white
                                        )
                                    )
                                }
                                "1" -> {
                                    holder.status.setImageResource(R.drawable.ic_check)
                                    holder.status.clearColorFilter()
                                    holder.status.setColorFilter(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.white
                                        )
                                    )
                                }
                                "0" -> {
                                    holder.status.setImageResource(R.drawable.ic_clock)
                                    holder.status.clearColorFilter()
                                    holder.status.setColorFilter(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.thim_color
                                        )
                                    )
                                }
                                else -> {
                                    holder.status.setImageResource(R.drawable.ic_double_check)
                                    holder.status.clearColorFilter()
                                    holder.status.setColorFilter(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.thim_color
                                        )
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageNewAdapter_Backup.ViewHolder {

        val item = getItem(viewType)

        val res: Int = when {
            item != null -> {
                if (item.sender.equals(my_id, ignoreCase = true)) {
                    when (item.type) {
                        "T" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_right_m
                        } else {
                            R.layout.chat_right_t
                        }
                        "I" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_right_m
                        } else {
                            R.layout.chat_right_i
                        }
                        else -> R.layout.chat_right_m
                    }
                } else {
                    when (item.type) {
                        "T" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_left_m
                        } else {
                            R.layout.chat_left_t
                        }
                        "I" -> if (!item.replay_of.equals("null", ignoreCase = true) &&
                            !item.replay_of.equals("", ignoreCase = true)
                        ) {
                            R.layout.chat_left_m
                        } else {
                            R.layout.chat_left_i
                        }
                        else -> R.layout.chat_left_m
                    }
                }
            }
            else -> R.layout.chat_shimmer
        }

        val view = LayoutInflater.from(activity).inflate(res, parent, false)
        val hol = ViewHolder(view)
        return hol;


    }

    private fun Hasreplay(item: Chats, holder: ViewHolder) {
        Thread {
            message_view_model.selectChat(item.replay_of) { repla ->

                repla?.let { replay ->
                    var name = ""

                    userVewModel.selectUser(replay.sender, Item {
                        name = if (it?.id.equals(my_id)) "You" else it.name ?: ""
                    })

                    val posis = findPosi(this.snapshot(), replay)
                    handler.post {
                        holder.replaySender.text = name
                        holder.replayText.text = replay.message

                        if (replay.type.equals("T", ignoreCase = true)) {
                            // Handle type T
                        } else if (replay.type.equals(
                                "I",
                                ignoreCase = true
                            ) && (replay.message == null || replay.message.equals(
                                "",
                                ignoreCase = true
                            ) || replay.message.equals("null", ignoreCase = true))
                        ) {
                            holder.replayText.visibility = View.GONE
                            val file = File(CATCH_DIR2 + "/" + replay.attachment)
                            if (file.exists()) {
                                Glide.with(context).load(file.absolutePath)
                                    .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
                                    .into(holder.ivReplayDoc)
                            } else {
                                Glide.with(context)
                                    .load(BASE_URL + "Attachment/" + replay.attachment)
                                    .thumbnail(0.05f)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .addListener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                            isFirstResource: Boolean
                                        ): Boolean {

                                            return true
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            resource?.let {
                                                holder.ivReplayDoc.setImageDrawable(resource)
                                            }
                                            return false
                                        }
                                    })
                                    .into(holder.ivReplayDoc)
                            }
                            holder.ivReplayAttachmentTitle.visibility = View.GONE
                            holder.layReplayDoc.visibility = View.VISIBLE
                        } else if (replay.type.equals(
                                "V",
                                ignoreCase = true
                            ) && (replay.message == null || replay.message.equals(
                                "",
                                ignoreCase = true
                            ) || replay.message.equals("null", ignoreCase = true))
                        ) {
                            holder.replayText.visibility = View.GONE
                            if (replay.attachment != null && !replay.attachment.equals("null")) {
                                val file = File(CATCH_DIR2 + "/" + replay.attachment)
                                if (file.exists()) {
                                    Thread {
                                        val futureTarget = Glide.with(context)
                                            .asBitmap()
                                            .override(300, 300)
                                            .load(file.absolutePath)
                                            .submit()
                                        try {
                                            val bitmap = futureTarget.get()
                                            handler.post {
                                                Glide.with(context).load(bitmap).override(300, 300)
                                                    .into(holder.ivReplayDoc)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }.start()
                                    holder.ivReplayAttachmentTitle.visibility = View.GONE
                                } else {
                                    holder.ivReplayAttachmentTitle.text = replay.attachment
                                }
                                holder.layReplayDoc.visibility = View.VISIBLE
                            }
                        } else if (replay.type.equals(
                                "P",
                                ignoreCase = true
                            ) || replay.type.equals(
                                "P",
                                ignoreCase = true
                            ) && (replay.message == null || replay.message.equals(
                                "",
                                ignoreCase = true
                            ) || replay.message.equals("null", ignoreCase = true))
                        ) {
                            holder.replayText.visibility = View.GONE
                            holder.ivReplayAttachmentTitle.text = replay.attachment
                            holder.layReplayDoc.visibility = View.VISIBLE
                        }

                        if (posis != -1) {
                            holder.layReplay.setOnClickListener { view ->
                                recycler.smoothScrollToPosition(posis)
                                val viewHolder =
                                    recycler.findViewHolderForAdapterPosition(posis) as? ViewHolder
                                viewHolder?.let {
                                    MethodClass.chengeBackground(it.mainLay)
                                }
                            }
                        }
                        holder.layReplay.visibility = View.VISIBLE
                    }
                }
            }

        }.start()
    }

    private fun findPosi(snapshot: ItemSnapshotList<Chats>, repla: Chats): Int {
        var posi = -1

        for (i in 0 until snapshot.size) {
            if (repla.id.equals(snapshot[i]?.id, ignoreCase = true)) {
                posi = i
                return posi
            }
        }

        return posi
    }

    private fun Download_file(
        item: Chats,
        iv_download: ImageView,
        iv_download_progress: ProgressBar,
        position: Int
    ) {
        iv_download.visibility = View.GONE
        iv_download_progress.visibility = View.VISIBLE

        val file = File("$CATCH_DIR2/${item.attachment}")
        val saveWithProgress = SaveWithProgress(
            "$Attachment_URL/${item.attachment}",
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

    private fun AttachmentDoc(item: Chats, holder: ViewHolder) {
        holder.layDoc.visibility = View.VISIBLE
        holder.ivAttachmentTitle.text = item.attachment
        holder.ivDoc.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_files))
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("$CATCH_DIR2/${item.attachment}")
            if (file.exists()) {
                holder.ivDownload.visibility = View.GONE
            }
        }
    }

    private fun AttachmentPDF(item: Chats, holder: ViewHolder) {
        holder.layDoc.visibility = View.VISIBLE
        holder.ivAttachmentTitle.text = item.attachment
        holder.ivDoc.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pdf))
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("$CATCH_DIR2/${item.attachment}")
            if (file.exists()) {
                holder.ivDownload.visibility = View.GONE
            }
        }
    }

    private fun AttachmentVideo(item: Chats, holder: ViewHolder) {
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("$CATCH_DIR2/${item.attachment}")
            if (file.exists()) {
                holder.ivAttachment.visibility = View.VISIBLE
                holder.layAttachment.visibility = View.VISIBLE
                holder.btnPlay.visibility = View.VISIBLE
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
                                .into(holder.ivAttachment)
                            holder.ivAttachment.visibility = View.VISIBLE
                            holder.layAttachment.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                holder.layDoc.visibility = View.VISIBLE
                holder.ivDownload.visibility = View.VISIBLE
                holder.ivAttachmentTitle.text = item.attachment
                holder.ivDoc.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_video
                    )
                )
            }

            holder.ivAttachment.setOnClickListener {
                MethodClass.showFullScreen(activity, handler, item.thread, item.id)
            }
        }
    }

    private fun showPopupMenu(chatMe: LinearLayout, item: Chats) {
        MethodClass.show_popup_menu2(chatMe, activity, object : OnMenuItemClick {
            override fun OnClick(res: Int) {
                if (res == R.id.menu_delete) {
                    if (item.sender.equals(my_id, ignoreCase = true)) {
                        message_view_model.updateDelete(item.id, "1")
                    } else {
                        message_view_model.updateDelete(item.id, "1")
                    }
                } else if (res == R.id.menu_foroward) {
                    // Handle menu_foroward
                    // Intent intent = Intent(activity, ShareActivity::class.java)
                    // intent.putExtra("from", "forowrd")
                    // intent.putExtra("type", item.type)
                    // intent.putExtra("msg", item.message ?: "")
                    // if (item.attachment != null) {
                    //     intent.putExtra("file", CATCH_DIR2 + "/" + item.attachment)
                    // } else {
                    //     intent.putExtra("file", "")
                    // }
                    // context.startActivity(intent)
                } else if (res == R.id.menu_report) {
                    MethodClass.report(activity, object : AllInterFace() {
                        override fun isClicked(`is`: Boolean) {
                            super.isClicked(`is`)
                            if (`is`) {
                                // Handle report submission
                            }
                            Toast.makeText(context, "Report Submitted", Toast.LENGTH_SHORT).show()
                        }
                    }, "This Group")
                }
            }
        })
    }

    private fun attachmentImage(item: Chats, holder: ViewHolder) {
        if (item.attachment != null && item.attachment != "null") {
            val file = File(CATCH_DIR2 + "/" + item.attachment)
            if (file.exists()) {
                MethodClass.GetFileBitmap(file.absolutePath, holder.ivAttachment, context)
                    .execute()
            } else {
                Glide.with(context)
                    .load(BASE_URL + "Attachment/" + item.attachment)
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.ivAttachment.setImageDrawable(context.getDrawable(R.drawable.ic_x))
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.ivAttachment.setImageDrawable(resource)
                            return false
                        }
                    })
                    .into(holder.ivAttachment)
                MethodClass.CashImage2(BASE_URL + "Attachment/" + item.attachment, item.attachment)
            }
            holder.ivAttachment.visibility = View.VISIBLE
            holder.layAttachment.visibility = View.VISIBLE
            holder.ivAttachment.setOnClickListener {
                MethodClass.showFullScreen(activity, handler, item.thread, item.id)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLay: LinearLayout = itemView.findViewById(R.id.main_lay)
        val message: TextView = itemView.findViewById(R.id.message)
        val time: TextView = itemView.findViewById(R.id.time)
        val sender: TextView = itemView.findViewById(R.id.sender)
        val ivAttachmentTitle: TextView = itemView.findViewById(R.id.iv_attachment_title)
        val layDoc: LinearLayout = itemView.findViewById(R.id.lay_doc)
        val layAttachment: RelativeLayout = itemView.findViewById(R.id.lay_attachment)
        val ivDownload: ImageView = itemView.findViewById(R.id.iv_download)
        val ivDoc: ImageView = itemView.findViewById(R.id.iv_doc)
        val btnPlay: ImageView = itemView.findViewById(R.id.btn_play)
        val ivAttachment: ImageView = itemView.findViewById(R.id.iv_attachment)
        val ivDownloadProgress: ProgressBar = itemView.findViewById(R.id.iv_download_progress)
        val status: ImageView = itemView.findViewById(R.id.status)
        val replaySender: TextView = itemView.findViewById(R.id.replay_sender)
        val replayText: TextView = itemView.findViewById(R.id.replay_text)
        val ivReplayAttachmentTitle: TextView =
            itemView.findViewById(R.id.iv_replay_attachment_title)
        val layReplay: LinearLayout = itemView.findViewById(R.id.lay_replay)
        val layReplayDoc: LinearLayout = itemView.findViewById(R.id.lay_replay_doc)
        val ivReplayDoc: ImageView = itemView.findViewById(R.id.iv_replay_doc)

    }

}