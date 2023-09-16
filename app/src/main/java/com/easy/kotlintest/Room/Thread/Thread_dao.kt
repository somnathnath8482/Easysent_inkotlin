package com.easy.kotlintest.Room.Thread

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.easy.kotlintest.Room.Users.Users

@Dao
interface Thread_dao {
    @Insert
    fun insert(thread: Message_Thread?)

    @Query("UPDATE Message_Thread SET last_message =:message , last_message_type=:type, last_message_status =:status,last_message_time =:time,unread = unread+1   WHERE thread_id =:id")
    fun update_lastseen(
        message: String?,
        status: String?,
        type: String?,
        id: String?,
        time: String?
    )

    @Query("DELETE  FROM Message_Thread WHERE thread_id =:id ")
    fun delete(id: String?)

    @Query("DELETE FROM Message_Thread")
    fun deleteAll()

    @get:Query("SELECT * FROM Message_Thread ORDER BY RID")
    val all: PagingSource<Int, Message_Thread>

    @Query("SELECT * FROM Message_Thread WHERE thread_id = :id")
    fun selectthread(id: String?): Message_Thread?


    @Query("select *  from Users  U   join message_thread T on ( T.sender==:id AND   u.user_id = T.reciver  ) OR  ( T.reciver =:id AND   u.user_id = T.sender  ) order by last_message_time DESC")
    fun getActiveThread(id: String?): PagingSource<Int, Message_Thread>

    @Query("UPDATE message_thread set unread=0 where thread_id=:id")
    fun updateUnread(id: String?)
}
