package com.easy.kotlintest.adapter.Message

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestListener
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    var myId: String = PrefUtill.getUser()?.user?.id ?: "",
) : PagingDataAdapter<Chats, RecyclerView.ViewHolder>(diffCallback, mainDispatcher),
    CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    /* override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         //Log.e("TAG", "onBindViewHolder: $position" )
         val item = getItem(position)
         item?.also {
             holder.message?.text =it.message
             holder.time?.text = MethodClass.changeDateFormat2(it.createdAt)
             holder.itemView.setOnLongClickListener { view: View? ->
                 showPopupMenu(holder.mainLay!!, item)
                 true
             }

             launch {
                 when {
                     item.type.equals("T", ignoreCase = true) -> {
                         // TODO: Handle type T
                     }
                     item.type.equals("I", ignoreCase = true) -> {
                       //  attachmentImage(item, holder)
                     }
                     item.type.equals("V", ignoreCase = true) -> {

                       //  AttachmentVideo(item, holder)
                     }
                     item.type.equals("P", ignoreCase = true) -> {
                       // AttachmentPDF(item, holder)
                     }
                     else -> {
                       // AttachmentDoc(item, holder)
                     }
                 }

                 if (!item.type.equals("T", ignoreCase = true) && !item.type.equals(
                         "I",
                         ignoreCase = true
                     )
                 ) { holder.ivDownload?.setOnClickListener { view ->
                         Download_file(
                             item,
                             holder.ivDownload,
                             holder.ivDownloadProgress!!,
                             position
                         )
                     } }


                 if (item.sender.equals(myId)){
                     when (item.seen) {
                         "2" -> {
                             holder.status?.setImageResource(R.drawable.ic_double_check)
                             holder.status?.clearColorFilter()
                             holder.status?.setColorFilter(
                                 ContextCompat.getColor(
                                     context,
                                     R.color.white
                                 )
                             )
                         }
                         "1" -> {
                             holder.status?.setImageResource(R.drawable.ic_check)
                             holder.status?.clearColorFilter()
                             holder.status?.setColorFilter(
                                 ContextCompat.getColor(
                                     context,
                                     R.color.white
                                 )
                             )
                         }
                         "0" -> {
                             holder.status?.setImageResource(R.drawable.ic_clock)
                             holder.status?.clearColorFilter()
                             holder.status?.setColorFilter(
                                 ContextCompat.getColor(
                                     context,
                                     R.color.thim_color
                                 )
                             )
                         }
                         else -> {
                             holder.status?.setImageResource(R.drawable.ic_double_check)
                             holder.status?.clearColorFilter()
                             holder.status?.setColorFilter(
                                 ContextCompat.getColor(
                                     context,
                                     R.color.thim_color
                                 )
                             )
                         }
                     }
                 }

             }
         }


     }*/

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
                }
                is LeftTextReplayHolder -> {
                    viewholder.binding.sender.visibility = View.GONE

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()
                }
                is RightTextReplayHolder -> {

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()
                }
                is LeftAttachmentHolder -> {
                    viewholder.binding.sender.visibility = View.GONE

                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()


                    when (item.type) {
                        "T" -> {

                        }
                        "I" -> {
                            attachmentImage(item, viewholder.binding.ivAttachment)
                        }
                        "V" -> {

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
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        else -> {
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                    }

                }
                is RightAttachmentHolder -> {
                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()


                    when (item.type) {
                        "T" -> {

                        }
                        "I" -> {
                            attachmentImage(item, viewholder.binding.ivAttachment)
                        }
                        "V" -> {

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
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        else -> {
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
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
                            attachmentImage(item, viewholder.binding.ivAttachment)
                        }
                        "V" -> {
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
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        else -> {
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                    }
                }
                is RightAllHolder -> {
                    viewholder.binding.message.text = item.message
                    viewholder.binding.time.text = item.getFormattedTime()


                    when (item.type) {
                        "T" -> {

                        }
                        "I" -> {
                            attachmentImage(item, viewholder.binding.ivAttachment)
                        }
                        "V" -> {

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
                            attachmentPDF(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                        "D" -> {
                            attachmentDoc(
                                item,
                                viewholder.binding.ivDownload,
                                viewholder.binding.ivAttachment,
                                viewholder.binding.ivDownloadProgress,
                                position
                            )
                        }
                    }
                }
            }

        }


    }


    private fun attachmentImage(item: Chats, imageview: ImageView) {
        if (item.attachment != null && item.attachment != "null") {
            val file = File(Constants.CATCH_DIR_Memory + "/" + item.attachment)
            if (file.exists()) {
                Glide.with(context)
                    .asBitmap()
                    .override(500, 300)
                    .load(file)
                    .into(imageview)
                /*

                   MethodClass.GetFileBitmap(
                       file.absolutePath,
                       imageview,
                       context
                   ).execute()*/
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
            imageview.setOnClickListener {
                MethodClass.showFullScreen(activity, handler, item.thread, item.id)
            }
        }
    }

    private fun attachmentDoc(
        item: Chats,
        download: ImageView,
        iv_doc: ImageView,
        progress: ProgressBar,
        position: Int
    ) {
        Glide.with(context).load(AppCompatResources.getDrawable(context, R.drawable.ic_files))
            .override(300, 300).into(iv_doc)
        iv_doc.visibility = View.VISIBLE
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {
                download.visibility = View.GONE
            } else {
                download.visibility = View.VISIBLE

                download.setOnClickListener {
                    downloadfile(
                        item,
                        download,
                        progress,
                        position
                    )
                }

            }
        }
    }

    private fun attachmentPDF(
        item: Chats,
        download: ImageView,
        iv_doc: ImageView,
        ivDownloadProgress: ProgressBar,
        position: Int
    ) {
        Glide.with(context).load(AppCompatResources.getDrawable(context, R.drawable.ic_pdf))
            .override(300, 300).into(iv_doc)
        iv_doc.visibility = View.VISIBLE
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {
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

    class ShimmerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


}