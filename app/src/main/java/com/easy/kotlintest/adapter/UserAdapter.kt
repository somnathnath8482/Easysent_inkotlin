package com.easy.kotlintest.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.easy.kotlintest.Helper.ImageGetter
import com.easy.kotlintest.Networking.Helper.Constants.BASE_URL
import com.easy.kotlintest.Networking.Helper.Constants.CATCH_DIR
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Helper.MethodClass.CashImage
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Users.Users
import com.easy.kotlintest.databinding.UserItemBinding
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File


class UserAdapter(
    diffCallback: DiffUtil.ItemCallback<Users>,
    mainDispatcher: CoroutineDispatcher,
    private var allInterface: AllInterFace
) : PagingDataAdapter<Users, UserAdapter.ViewHolder>(diffCallback, mainDispatcher) {


    private lateinit var context: Context


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position) ?: Users()
        holder.binding.name.text = user.name
        holder.binding.about.text = user.about ?: "Hi! Happy To Use EasySent \uD83D\uDC95"

        if (user.profilePic != null && !user.profilePic.equals("null")) {
            val url: String = BASE_URL + "profile_image/" + user.profilePic
            val file: File = File(CATCH_DIR + "/" + user.profilePic)
            if (file.exists()) {
                Thread {
                    val imageGetter = ImageGetter(holder.binding.profileImage)
                    imageGetter.execute(file)
                }.start()
            } else {
                Glide.with(context).load(url).thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .addListener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.binding.profileImage.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    context, MethodClass.getResId(
                                        user.name, Drawable::class.java
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
                            holder.binding.profileImage.setImageDrawable(resource)
                            CashImage(
                                BASE_URL + "profile_image/" + user.profilePic,
                                user.profilePic,
                                resource
                            )
                            return false
                        }
                    }).into(holder.binding.profileImage)
            }
        } else {
            holder.binding.profileImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    context, MethodClass.getResId(
                        user.name, Drawable::class.java
                    )
                )
            )
        }


        if (user.fstatus.equals("Online")) {
            holder.binding.status.background = null
            holder.binding.status.background =
                AppCompatResources.getDrawable(context, R.drawable.active)
            holder.binding.status.visibility = View.VISIBLE
        } else if (user.fstatus.equals("a")) {
            holder.binding.status.background = null
            holder.binding.status.background =
                AppCompatResources.getDrawable(context, R.drawable.away)
            holder.binding.status.visibility = View.VISIBLE
        } else {
            holder.binding.status.visibility = View.GONE
        }


        holder.itemView.setOnClickListener {
            allInterface?.IsClicked(user.id)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context;
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = UserItemBinding.bind(itemView);

    }
}