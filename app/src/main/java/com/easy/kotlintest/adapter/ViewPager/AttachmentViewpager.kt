package com.easy.kotlintest.adapter.ViewPager

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.Constants.CATCH_DIR2
import com.easy.kotlintest.Networking.Helper.MethodClass.CashImage2
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.databinding.AttachmentItemBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.File


class AttachmentViewpager(chats: MutableList<Chats>, context: Activity, handler: Handler) :
    RecyclerView.Adapter<AttachmentViewpager.ViewHolder>() {


    private var chats: MutableList<Chats>
    private var context: Activity
    private var handler: Handler
    private var player: ExoPlayer?  = null
    private var playerList: MutableList<ExoPlayer> = ArrayList()

    init {
        this.chats = chats
        this.context = context
        this.handler = handler
       this. player = ExoPlayer.Builder(context).build()
    }


    fun pauseAll() {
        playerList.forEach {
            if (it?.isPlaying?:false){
                it.pause()
            }
        }
    }

    fun getPlayer(): ExoPlayer? {
        return player
    }

    fun releasePlayer() {
        player?.release()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: AttachmentItemBinding

        init {
            binding = AttachmentItemBinding.bind(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.attachment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding


        val item: Chats = chats.get(position)
        if (item.type.equals("I", true)) {
            val file = File(CATCH_DIR2 + "/" + item.attachment)
            if (file.exists() && file.isAbsolute) {
                Glide.with(context).load(file.absolutePath)
                    .into(binding.ivAttachment)
            } else {
                Glide.with(context).load(Constants.BASE_URL + "Attachment/" + item.attachment)
                    .thumbnail(0.02f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .addListener(object : RequestListener<Drawable?> {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.ivAttachment.setImageDrawable(context.getDrawable(R.drawable.ic_x))
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.ivAttachment.setImageDrawable(resource)
                            return false
                        }
                    })
                    .into(binding.ivAttachment)
                CashImage2(
                    Constants.BASE_URL + "Attachment/" + item.attachment,
                    item.attachment
                )
            }
            binding.ivAttachment.visibility = View.VISIBLE
        }
        if (item.type.equals("V", true)) {

                player?.pause()

            player = ExoPlayer.Builder(context).build()
            var path: String = Constants.BASE_URL + "Attachment/" + item.attachment
            val file = File(CATCH_DIR2 + "/" + item.attachment)
            if (file.exists()) {
                path = file.absolutePath
            }
            val mediaItem = MediaItem.fromUri(Uri.parse(path))
            player?.setMediaItem(mediaItem)
            player?.prepare()
            binding.vvAttachemt.player = player
            player?.playWhenReady = false
            binding.vvAttachemt.visibility = View.VISIBLE
            playerList.add(player!!)

            Log.e("TAG", playerList.size.toString())
        }

    }

    override fun getItemCount(): Int {
        return chats.size
    }
}


