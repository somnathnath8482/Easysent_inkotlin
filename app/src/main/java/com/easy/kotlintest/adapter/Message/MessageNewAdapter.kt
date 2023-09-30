package com.easy.kotlintest.adapter.Message

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Helper.SaveWithProgress
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Messages.Message_View_Model
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.databinding.ChatLeftAllBinding
import com.easy.kotlintest.databinding.ChatLeftAttachmentBinding
import com.easy.kotlintest.databinding.ChatLeftTextBinding
import com.easy.kotlintest.databinding.ChatLeftTextReplayBinding
import com.easy.kotlintest.databinding.ChatRightAllBinding
import com.easy.kotlintest.databinding.ChatRightAttachmentBinding
import com.easy.kotlintest.databinding.ChatRightTextBinding
import com.easy.kotlintest.databinding.ChatRightTextReplayBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    /* override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
/*    private fun showPopupMenu(chatMe: LinearLayout, item: Chats) {
        MethodClass.show_popup_menu2(chatMe, activity
        ) { res ->
            if (res == R.id.menu_delete) {
                    message_view_model.updateDelete(item.id, "1")
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
    }


    private fun AttachmentDoc(item: Chats, holder:ViewHolder) {
        holder.layDoc?.visibility = View.VISIBLE
        holder.ivAttachmentTitle?.text = item.attachment
        holder.ivDoc?.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_files))
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {
                holder.ivDownload?.visibility = View.GONE
            }
        }
    }

    private fun AttachmentPDF(item: Chats, holder: ViewHolder) {
        holder.layDoc?.visibility = View.VISIBLE
        holder.ivAttachmentTitle?.text = item.attachment
        holder.ivDoc?.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pdf))
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {
                holder.ivDownload?.visibility = View.GONE
            }
        }
    }

    private fun attachmentImage(item: Chats, holder: ViewHolder) {
        if (item.attachment != null && item.attachment != "null") {
            val file = File(Constants.CATCH_DIR_Memory + "/" + item.attachment)
            if (file.exists()) {
                MethodClass.GetFileBitmap(file.absolutePath, holder.ivAttachment, context)
                    .execute()
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
                                target: Target<Bitmap?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                try {
                                    holder.ivAttachment!!.setImageBitmap(resource)
                                    MethodClass.CashImageInMemoryOriginalQuality(item.attachment,resource)
                                }catch (e:Exception){
                                    e.printStackTrace()

                                }
                                return false
                            }
                        })
                        .submit()



                }



            }
            holder.ivAttachment?.visibility = View.VISIBLE
            holder.layAttachment?.visibility = View.VISIBLE
            holder.ivAttachment?.setOnClickListener {
                MethodClass.showFullScreen(activity, handler, item.thread, item.id)
            }
        }
    }
    private fun AttachmentVideo(item: Chats, holder: ViewHolder) {
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {
                holder.ivAttachment?.visibility = View.VISIBLE
                holder.layAttachment?.visibility = View.VISIBLE
                holder.btnPlay?.visibility = View.VISIBLE

                val cashFile = File(Constants.CATCH_DIR_CASH + "/" + item.attachment)
                if (cashFile.exists()){
                    Glide.with(context)
                        .load(cashFile)
                        .override(600, 600)
                        .into(holder.ivAttachment!!)
                    holder.ivAttachment?.visibility = View.VISIBLE
                    holder.layAttachment?.visibility = View.VISIBLE
                }else {


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
                                    .into(holder.ivAttachment!!)
                                holder.ivAttachment?.visibility = View.VISIBLE
                                holder.layAttachment?.visibility = View.VISIBLE
                            }
                            MethodClass.CashImageInCatch(
                                file.name.replace("mp4","png"),
                                bitmap
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                }
            } else {
                holder.layDoc?.visibility = View.VISIBLE
                holder.ivDownload?.visibility = View.VISIBLE
                holder.ivAttachmentTitle?.text = item.attachment
                holder.ivDoc?.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_video
                    )
                )
            }

            holder.ivAttachment?.setOnClickListener {
                MethodClass.showFullScreen(activity, handler, item.thread, item.id)
            }
        }
    }*/

    private fun Download_file(
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

        init {
            val binding = ChatLeftAllBinding.bind(itemView)
            binding.message.text = "CHAT LEFT ALL"

        }


    }

    class LeftAttachmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            val binding = ChatLeftAttachmentBinding.bind(itemView)
            binding.message.text = "CHAT LEFT  ATTACHMENT"
        }

    }

    class LeftTextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val binding = ChatLeftTextBinding.bind(itemView)
            binding.message.text = "CHAT LEFT TEXT"

        }

    }
    class LeftTextReplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val binding = ChatLeftTextReplayBinding.bind(itemView)
            binding.message.text = "CHAT LEFT  TEXT REPLAY"

        }

    }

    class RightAllHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            val binding = ChatRightAllBinding.bind(itemView)
            binding.message.text = "CHAT RIGHT  ALL"
        }


    }

    class RightAttachmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

            val binding = ChatRightAttachmentBinding.bind(itemView)
            binding.message.text = "CHAT Right Attachment"
        }

    }

    class RightTextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            val binding = ChatRightTextBinding.bind(itemView)
            binding.message.text = "CHAT RIGHT TEXT"
        }

    }
    class RightTextReplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            val binding = ChatRightTextReplayBinding.bind(itemView)
            binding.message.text = "CHAT RIGHT TEXT REPLAY"
        }


    }

    class ShimmerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }


}