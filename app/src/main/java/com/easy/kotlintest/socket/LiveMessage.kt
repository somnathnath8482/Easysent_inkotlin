package com.easy.kotlintest.socket

import android.app.Application
import android.util.Log
import com.bumptech.glide.Glide
import com.easy.kotlintest.Encription.Encripter
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Room.Messages.Chatepository
import com.easy.kotlintest.Room.Messages.Chats
import com.easy.kotlintest.Room.Thread.Thread_repository
import com.easy.kotlintest.Room.Users.UserRepository
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket


class LiveMessage {


    companion object {
        private lateinit var mSocket: Socket
        private lateinit var messarepository: Chatepository
        private lateinit var threadRepository: Thread_repository
        private lateinit var userRepository: UserRepository
        private lateinit var encripter: Encripter
        private lateinit var showNotification: ShowNotification
        private lateinit var application: Application
        fun listen(reciver: String?) {

            mSocket.on(reciver) { args ->
                val customMessage: CustomMessage = Gson().fromJson(args[0].toString(), CustomMessage::class.java)
                val chats = customMessage.chats
                Log.e("SOCKET", "new message: $chats")
                messarepository.insert(chats)
                val decripter = Encripter(chats.sender);
                threadRepository.update_last_seen(
                    decripter.decrepit(chats.message),
                    "0",
                    chats.type,
                    chats.thread,
                    chats.createdAt
                )


                encripter = Encripter(chats.sender)

                if (chats.sender != Constants.ACTIVE) {
                    val futureTarget = Glide.with(application)
                        .asBitmap()
                        .load(Constants.BASE_URL + "profile_image/" + customMessage.profile_image)
                        .submit()

                    try {
                        showNotification.showNotification(
                            application,
                            chats.sender?.toInt()!!,
                            encripter.decrepit(chats.message),
                            chats.sender_name,
                            futureTarget.get()
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            };
        }

        fun sendMessage(chats: Chats, profilePic: String?) {
            val json = Gson()
            val customMessage = CustomMessage(profilePic,chats,chats.reciver)
            mSocket.emit("NM", json.toJson(customMessage,CustomMessage::class.java));
        }
    }


    constructor(application: Application) {
        try {
            Companion.application = application
            mSocket = IO.socket("http://worldtoday.online:4000")
            mSocket.let {
                it.connect()
                    .on(Socket.EVENT_CONNECT) {
                        Log.d("SignallingClient", "Socket connected!!!!!")
                        Log.e("SOCKET", "status: ${mSocket.connected()}")
                    }
                    .on(Socket.EVENT_CONNECT_ERROR) { args ->
                        Log.e("SOCKET", "ERROR: ${args[0].toString()}")
                    };


                messarepository = Chatepository(application)
                threadRepository = Thread_repository(application)
                userRepository = UserRepository(application)
                showNotification = ShowNotification()
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SOCKET", "error: ${e.stackTrace}")
        }
    }


}