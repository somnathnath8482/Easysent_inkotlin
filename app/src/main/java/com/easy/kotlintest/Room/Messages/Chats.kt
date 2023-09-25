package com.easy.kotlintest.Room.Messages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(indices = [Index(value = ["chat_id"], unique = true)])
class Chats {
    @PrimaryKey(autoGenerate = true)
    var rid = 0

    @SerializedName("attachment")
    var attachment: String? = ""

    @SerializedName("sender")
    var sender: String? = ""

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

    @SerializedName("is_deleted")
    var deleted: String? = ""

    constructor() {}
    constructor(
        attachment: String?, sender: String?, createdAt: String?, id: String?,
        thread: String?, message: String?, type: String?, reciver: String?,
        seen: String?,
        replay_of: String?,
        is_deleted: String?
    ) {
        this.attachment = attachment
        this.sender = sender
        this.createdAt = createdAt
        this.id = id
        this.thread = thread
        this.message = message
        this.type = type
        this.reciver = reciver
        this.seen = seen
        this.replay_of = replay_of
        this.deleted = is_deleted
    }
}
