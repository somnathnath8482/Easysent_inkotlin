package com.easy.kotlintest.Room.Thread

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value = ["thread_id"], unique = true)])
class Message_Thread {
    @PrimaryKey(autoGenerate = true)
    var rid = 0
    var sender: String? = null

    @ColumnInfo(name = "thread_id")
    var id: String? = null
    var createAt: String? = null
    var reciver: String? = null
    var unread = 0
    var last_message: String? = null
    var last_message_type: String? = null
    var last_message_status: String? = null
    var last_message_time: String? = null

    constructor() {}
    constructor(
        sender: String?,
        id: String?,
        createAt: String?,
        reciver: String?,
        last_message: String?,
        last_message_type: String?,
        last_message_status: String?,
        last_message_time: String?
    ) {
        this.sender = sender
        this.id = id
        this.createAt = createAt
        this.reciver = reciver
        this.last_message = last_message
        this.last_message_type = last_message_type
        this.last_message_status = last_message_status
        this.last_message_time = last_message_time
    }
}
