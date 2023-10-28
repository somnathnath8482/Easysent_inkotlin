package com.easy.kotlintest.adapter.Message

/**
 * Created by Somnath nath on 11,October,2023
 * Artix Development,
 * India.
 */
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.Log
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.CoroutineContext


class MessageNewAdapter internal constructor(
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
    /* override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
         when (viewType) {
             ViewTypes.SMALL.type -> SmallItemViewHolder(SmallItemCell(parent.context).apply { inflate() })
             else -> LargeItemViewHolder(LargeItemCell(parent.context).apply { inflate() })
         }*/


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.chat_shimmer -> ShimmerHolder(ShimmerCell(parent.context).apply { inflate() })

            R.layout.chat_left_all -> LeftAllHolder(LeftAllCell(parent.context).apply { inflate() })
            R.layout.chat_left_text -> LeftTextHolder(LeftTextCell(parent.context).apply { inflate() })
            R.layout.chat_left_attachment -> LeftAttachmentHolder(LeftAttachmentCell(parent.context).apply { inflate() })
            R.layout.chat_left_text_replay -> LeftTextReplayHolder(LeftTextReplayCell(parent.context).apply { inflate() })

            R.layout.chat_right_all -> RightAllHolder(RightAllCell(parent.context).apply { inflate() })
            R.layout.chat_right_text -> RightTextHolder(RightTextCell(parent.context).apply { inflate() })
            R.layout.chat_right_attachment -> RightAttachmentHolder(RightAttachmentCell(parent.context).apply { inflate() })
            R.layout.chat_right_text_replay -> RightTextReplayHolder(RightTextReplayCell(parent.context).apply { inflate() })
            else -> ShimmerHolder(ShimmerCell(parent.context).apply { inflate() })
        }


    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Log.e("TAG", "onBindViewHolder: $position" )
        when (holder) {
            is LeftTextHolder -> {
                setUpLeftTextViewHolder(holder, position)
            }
            is RightTextHolder -> {
                setUpRightTextViewHolder(holder, position)
            }
            is LeftTextReplayHolder -> {
                setUpLeftTextReplayViewHolder(holder, position)
            }
            is RightTextReplayHolder -> {
                setUpRightTextReplayViewHolder(holder, position)
            }
            is LeftAttachmentHolder -> {
                setUpLeftAttachmentViewHolder(holder, position)
            }
            is RightAttachmentHolder -> {
                setUpRightAttachmentViewHolder(holder, position)
            }
            is LeftAllHolder -> {
                setUpLeftAllViewHolder(holder, position)
            }
            is RightAllHolder -> {
                setUpRightAllViewHolder(holder, position)
            }
        }
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



    /*-------------------------*/

    private fun setUpLeftAllViewHolder(viewholder: LeftAllHolder, position: Int) {
        (viewholder.itemView as LeftAllCell).bindWhenInflated {
            getItem(position).let { item ->

                val binding = ChatLeftAllBinding.bind(viewholder.itemView)
                binding.sender.visibility = View.GONE

                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()

                when (item?.type) {
                    "T" -> {

                    }
                    "I" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentImage(
                            item,
                            binding.ivAttachment,
                            binding.btnPlay,
                            binding.ivDownloadProgress,
                            binding.fileType
                        )
                    }
                    "V" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentVideo(
                            item,
                            binding.ivDownload,
                            binding.btnPlay,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    "P" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentPDF(
                            item,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    else -> {
                        if (item?.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentDoc(
                            item!!,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                }

            }
        }
    }

    private fun setUpLeftAttachmentViewHolder(viewholder: LeftAttachmentHolder, position: Int) {
        (viewholder.itemView as LeftAttachmentCell).bindWhenInflated {
            getItem(position).let { item ->

                val binding = ChatLeftAttachmentBinding.bind(viewholder.itemView)
                binding.sender.visibility = View.GONE

                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()


                when (item?.type) {
                    "T" -> {

                    }
                    "I" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentImage(
                            item,
                            binding.ivAttachment,
                            binding.btnPlay,
                            binding.ivDownloadProgress,
                            binding.fileType
                        )
                    }
                    "V" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentVideo(
                            item,
                            binding.ivDownload,
                            binding.btnPlay,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    "P" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentPDF(
                            item,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    else -> {
                        if (item?.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentDoc(
                            item!!,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                }

            }
        }
    }

    private fun setUpLeftTextViewHolder(viewholder: LeftTextHolder, position: Int) {
        (viewholder.itemView as LeftTextCell).bindWhenInflated {
            getItem(position).let { item ->
                val binding = ChatLeftTextBinding.bind(viewholder.itemView)
                binding.sender.visibility = View.GONE

                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()
            }
        }
    }

    private fun setUpLeftTextReplayViewHolder(viewholder: LeftTextReplayHolder, position: Int) {
        (viewholder.itemView as LeftTextReplayCell).bindWhenInflated {
            getItem(position).let { item ->
                val binding = ChatLeftTextReplayBinding.bind(viewholder.itemView)
                binding.sender.visibility = View.GONE

                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()
            }
        }
    }

    /*---------------------*/
    private fun setUpRightAllViewHolder(viewholder: RightAllHolder, position: Int) {
        (viewholder.itemView as RightAllCell).bindWhenInflated {
            getItem(position).let { item ->
                val binding = ChatRightAllBinding.bind(viewholder.itemView)


                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()

                status(item!!, binding.status)

                when (item?.type) {
                    "T" -> {

                    }
                    "I" -> {
                        if (item?.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentImage(
                            item,
                            binding.ivAttachment,
                            binding.btnPlay,
                            binding.ivDownloadProgress,
                            binding.fileType
                        )
                    }
                    "V" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentVideo(
                            item,
                            binding.ivDownload,
                            binding.btnPlay,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    "P" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentPDF(
                            item,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    else -> {
                        if (item?.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentDoc(
                            item,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                }

            }
        }
    }

    private fun setUpRightAttachmentViewHolder(viewholder: RightAttachmentHolder, position: Int) {
        (viewholder.itemView as RightAttachmentCell).bindWhenInflated {
            getItem(position).let { item ->
                val binding = ChatRightAttachmentBinding.bind(viewholder.itemView)


                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()

                status(item!!, binding.status)

                when (item?.type) {
                    "T" -> {

                    }
                    "I" -> {
                        if (item?.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentImage(
                            item,
                            binding.ivAttachment,
                            binding.btnPlay,
                            binding.ivDownloadProgress,
                            binding.fileType
                        )
                    }
                    "V" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        attachmentVideo(
                            item,
                            binding.ivDownload,
                            binding.btnPlay,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    "P" -> {
                        if (item.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentPDF(
                            item,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                    else -> {
                        if (item?.message == null || item.message?.length == 0) {
                            binding.message.visibility = View.GONE
                        }
                        binding.btnPlay.visibility = View.GONE
                        attachmentDoc(
                            item,
                            binding.ivDownload,
                            binding.ivAttachment,
                            binding.ivDownloadProgress,
                            position,
                            binding.fileType
                        )
                    }
                }

            }
        }
    }

    private fun setUpRightTextViewHolder(viewholder: RightTextHolder, position: Int) {
        (viewholder.itemView as RightTextCell).bindWhenInflated {
            getItem(position).let { item ->
                val binding = ChatRightTextBinding.bind(viewholder.itemView)
                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()

                status(item!!, binding.status)
            }
        }
    }

    private fun setUpRightTextReplayViewHolder(viewholder: RightTextReplayHolder, position: Int) {
        (viewholder.itemView as RightTextReplayCell).bindWhenInflated {
            getItem(position).let { item ->
                val binding = ChatRightTextReplayBinding.bind(viewholder.itemView)
                binding.message.text = item?.message
                binding.time.text = item?.getFormattedTime()

                status(item!!, binding.status)
            }
        }
    }


    /*-------------------------*/


    @SuppressLint("SetTextI18n")
    class LeftAllHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // val binding = ChatLeftAllBinding.bind(itemView)
    }

    class LeftAttachmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // val binding = ChatLeftAttachmentBinding.bind(itemView)
    }

    class LeftTextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // val binding = ChatLeftTextBinding.bind(itemView)
    }

    class LeftTextReplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  val binding = ChatLeftTextReplayBinding.bind(itemView)
    }

    class RightAllHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  val binding = ChatRightAllBinding.bind(itemView)
    }

    class RightAttachmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // val binding = ChatRightAttachmentBinding.bind(itemView)
    }

    class RightTextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  val binding = ChatRightTextBinding.bind(itemView)
    }

    class RightTextReplayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //  val binding = ChatRightTextReplayBinding.bind(itemView)
    }

    class ShimmerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    /*-----------------------*/

    private inner class LeftAllCell(context: Context) : AsyncCell(context) {
        var binding: ChatLeftAllBinding? = null
        override val layoutId = R.layout.chat_left_all
        override fun createDataBindingView(view: View): View? {
            binding = ChatLeftAllBinding.bind(view)
            return view.rootView
        }
    }


    private inner class LeftAttachmentCell(context: Context) : AsyncCell(context) {
        var binding: ChatLeftAttachmentBinding? = null
        override val layoutId = R.layout.chat_left_attachment
        override fun createDataBindingView(view: View): View? {
            binding = ChatLeftAttachmentBinding.bind(view)
            return view.rootView
        }
    }

    private inner class LeftTextCell(context: Context) : AsyncCell(context) {
        var binding: ChatLeftTextBinding? = null
        override val layoutId = R.layout.chat_left_text
        override fun createDataBindingView(view: View): View? {
            binding = ChatLeftTextBinding.bind(view)
            return view.rootView
        }
    }


    private inner class LeftTextReplayCell(context: Context) : AsyncCell(context) {
        var binding: ChatLeftTextReplayBinding? = null
        override val layoutId = R.layout.chat_left_text_replay
        override fun createDataBindingView(view: View): View? {
            binding = ChatLeftTextReplayBinding.bind(view)
            return view.rootView
        }
    }


    private inner class RightAllCell(context: Context) : AsyncCell(context) {
        var binding: ChatRightAllBinding? = null
        override val layoutId = R.layout.chat_right_all
        override fun createDataBindingView(view: View): View? {
            binding = ChatRightAllBinding.bind(view)
            return view.rootView
        }
    }


    private inner class RightAttachmentCell(context: Context) : AsyncCell(context) {
        var binding: ChatRightAttachmentBinding? = null
        override val layoutId = R.layout.chat_right_attachment
        override fun createDataBindingView(view: View): View? {
            binding = ChatRightAttachmentBinding.bind(view)
            return view.rootView
        }
    }

    private inner class RightTextCell(context: Context) : AsyncCell(context) {
        var binding: ChatRightTextBinding? = null
        override val layoutId = R.layout.chat_right_text
        override fun createDataBindingView(view: View): View? {
            binding = ChatRightTextBinding.bind(view)
            return view.rootView
        }
    }


    private inner class RightTextReplayCell(context: Context) : AsyncCell(context) {
        var binding: ChatRightTextReplayBinding? = null
        override val layoutId = R.layout.chat_right_text_replay
        override fun createDataBindingView(view: View): View? {
            binding = ChatRightTextReplayBinding.bind(view)
            return view.rootView
        }
    }

    private inner class ShimmerCell(context: Context) : AsyncCell(context) {
        var binding: ChatShimmerBinding? = null
        override val layoutId = R.layout.chat_shimmer
        override fun createDataBindingView(view: View): View? {
            binding = ChatShimmerBinding.bind(view)
            return view.rootView
        }
    }

    /*-------------------*/

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
        ivDownloadProgress: ProgressBar,
        fileType: TextView
    ) {
        if (item.attachment != null && item.attachment != "null") {
            fileType.text = "IMAGE"

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
            ivDownloadProgress.max = 100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner) { value ->
                val res = value?.outputData?.getString("res")
                val progress = value?.progress?.getInt("progress", 0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        btnPlay.visibility = View.GONE
                        Log.e("progress", "progress: $progress")
                        ivDownloadProgress.setProgress(progress ?: 0, true)
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
            ivDownloadProgress.max = 100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner) { value ->
                val res = value?.outputData?.getString("res")
                val progress = value?.progress?.getInt("progress", 0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("progress", "progress: $progress")
                        ivDownloadProgress.setProgress(progress ?: 0, true)
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
        fileType.text = "PDF"
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            if (file.exists()) {

                val path =
                    FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )

                val cashFile =
                    File(Constants.CATCH_DIR_CASH + "/" + file.name.replace("pdf", "png"))
                if (cashFile.exists()) {
                    Glide.with(context).asBitmap().load(cashFile)
                        .override(1000, 1000).into(iv_doc)
                } else {

                    handler.post(kotlinx.coroutines.Runnable {

                        Glide.with(context)
                            .load(AppCompatResources.getDrawable(context, R.drawable.pdf_thumb))
                            .override(600, 500).into(iv_doc)
                    })

                    val mc = com.easy.kotlintest.Helper.MethodClass()
                    mc.getThumbnail(context.contentResolver, path) {

                        handler.post(kotlinx.coroutines.Runnable {

                            Glide.with(context).load(it)
                                .override(600, 500).into(iv_doc)
                        })

                        MethodClass.CashImageInCatchOriginalQuality(
                            file.name.replace("pdf", "png"),
                            it
                        )

                    }
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
            ivDownloadProgress.max = 100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner) { value ->
                val res = value?.outputData?.getString("res")
                val progress = value?.progress?.getInt("progress", 0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("progress", "progress: $progress")
                        ivDownloadProgress.setProgress(progress ?: 0, true)
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
        position: Int,
        fileType: TextView
    ) {
        if (!item.attachment.isNullOrBlank() && item.attachment != "null") {
            val file = File("${Constants.CATCH_DIR_Memory}/${item.attachment}")
            attachment.visibility = View.VISIBLE
            fileType.text = "VIDEO"

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
            ivDownloadProgress.max = 100
            manager.getWorkInfoByIdLiveData(item.workId!!).observe(owner) { value ->
                val res = value?.outputData?.getString("res")
                val progress = value?.progress?.getInt("progress", 0)


                try {
                    if (res != null && res != "" && value.state.name == "SUCCEEDED") {
                        val obj = JSONObject(res)
                        if (obj.has("error")) {
                            Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        play.visibility = View.GONE
                        Log.e("progress", "progress: $progress")
                        ivDownloadProgress.setProgress(progress ?: 0, true)
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


    /*-------------------*/


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}