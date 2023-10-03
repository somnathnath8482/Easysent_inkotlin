package com.easy.kotlintest.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.easy.kotlintest.Networking.Helper.Constants.BASE_URL
import com.easy.kotlintest.Networking.Helper.Constants.CATCH_DIR_CASH
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Thread.Active_Thread
import com.easy.kotlintest.Room.Users.Users
import com.easy.kotlintest.databinding.UserItemBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext


class ThreadAdapter(
    diffCallback: DiffUtil.ItemCallback<Active_Thread>,
    mainDispatcher: CoroutineDispatcher,
    private var allInterface: AllInterFace
) : PagingDataAdapter<Active_Thread, ThreadAdapter.ViewHolder>(diffCallback, mainDispatcher), CoroutineScope {


    private lateinit var context: Context


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position) ?: Active_Thread()
        holder.binding.name.text = user.name
        holder.binding.about.text = user.last_message ?: ""

        if (user.profilePic != null && !user.profilePic.equals("null")) {
            val url: String = BASE_URL + "profile_image/" + user.profilePic
            val file: File = File(CATCH_DIR_CASH + "/" + user.profilePic)
            if (file.exists()) {
                launch {
                        Glide.with(context)
                            .load(file)
                            .into(holder.binding.profileImage)
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
                            .into(holder.binding.profileImage)

                        MethodClass.CashImageInCatch(user.profilePic, bim)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

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

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}