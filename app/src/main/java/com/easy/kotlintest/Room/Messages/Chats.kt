package com.easy.kotlintest.Room.Messages

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(indices = [Index(value = ["chat_id"], unique = true)])
class Chats {
    @PrimaryKey(autoGenerate = true)
    var rid = 0

    @SerializedName("attachment")
    var attachment: String? = ""

    @SerializedName("sender")
    var sender: String? = ""

    @SerializedName("sender_name")
    var sender_name: String? = ""

    @SerializedName("created_at")
    var createdAt: String? = ""

    @ColumnInfo(name = "chat_id")
    var id: String? = ""

    @SerializedName("thread")
    var thread: String? = ""

    @SerializedName("message")
    var message: String? = ""

    @SerializedName("type")
    var type: String? = ""

    @SerializedName("replay_of")
    var replay_of: String? = ""

    @SerializedName("reciver")
    var reciver: String? = ""

    @SerializedName("seen")
    var seen: String? = ""

   @SerializedName("work_id")
    var workId : UUID?  = null

    @SerializedName("is_deleted")
    var deleted: String? = ""

    constructor() {}
    constructor(
        attachment: String?, sender: String?, sender_name: String?, createdAt: String?, id: String?,
        thread: String?, message: String?, type: String?, reciver: String?,
        seen: String?,
        work_id : UUID?,
        replay_of: String?,
        is_deleted: String?
    ) {
        this.attachment = attachment
        this.sender = sender
        this.sender_name = sender_name
        this.createdAt = createdAt
        this.id = id
        this.thread = thread
        this.message = message
        this.type = type
        this.reciver = reciver
        this.seen = seen
        this.workId = work_id
        this.replay_of = replay_of
        this.deleted = is_deleted
    }

    fun getFormattedTime(): String? {
        return MethodClass.changeDateFormat2(createdAt)
    }
    fun getFileType(): String? {
        val arr  = attachment?.split(".")
        Log.e("TAG", "getFileType: $attachment")
        return arr?.get(arr.size.minus(1))
    }
}
