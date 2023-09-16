package com.easy.kotlintest.Room.Thread

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName


 class Active_Thread {


     var rid = 0

    @SerializedName("phone")
     var phone: String? = null

    @SerializedName("name")
     var name: String? = null

    @SerializedName("profile_pic")
     var profilePic: String? = null


    @ColumnInfo(name = "user_id")
     var id: String? = null

    @SerializedName("fstatus")
     var fstatus: String? = null

    @SerializedName("gender")
     var gender: String? = null

    @SerializedName("about")
     var about: String? = null

    @SerializedName("is_verifyed")
     var isVerifyed: String? = null

    @SerializedName("email")
     var email: String? = null

    @SerializedName("token")
     var token: String? = null

    @SerializedName("unread")
     val unread = 0

    @SerializedName("cash_pic")
     val cash_pic: String? = null


     var sender: String? = null
     var thread_id: String? = null
     var createAt: String? = null
     var reciver: String? = null
     var last_message: String? = null
     var last_message_type: String? = null
     var last_message_status: String? = null
     var last_message_time: String? = null

    constructor()


    constructor(
        rid: Int,
        phone: String?,
        name: String?,
        profilePic: String?,
        id: String?,
        fstatus: String?,
        gender: String?,
        about: String?,
        isVerifyed: String?,
        email: String?,
        token: String?,
        sender: String?,
        thread_id: String?,
        createAt: String?,
        reciver: String?,
        last_message: String?,
        last_message_type: String?,
        last_message_status: String?,
        last_message_time: String?
    ) {
        this.rid = rid
        this.phone = phone
        this.name = name
        this.profilePic = profilePic
        this.id = id
        this.fstatus = fstatus
        this.gender = gender
        this.about = about
        this.isVerifyed = isVerifyed
        this.email = email
        this.token = token
        this.sender = sender
        this.thread_id = thread_id
        this.createAt = createAt
        this.reciver = reciver
        this.last_message = last_message
        this.last_message_type = last_message_type
        this.last_message_status = last_message_status
        this.last_message_time = last_message_time
    }


}