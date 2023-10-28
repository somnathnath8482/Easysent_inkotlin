package com.easy.kotlintest.Room.Messages

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ChatDao {
    @Insert
    fun insert(thread: Chats?)

    @Query("UPDATE Chats SET seen =:status  WHERE chat_id =:id")
    fun updateSeen(status: String?, id: String?)

    @Query("UPDATE Chats SET deleted=:value , replay_of='', message='This message is deleted', type='T' WHERE chat_id =:id")
    fun updateDeleted(id: String?, value: String?)

    @Query("UPDATE Chats SET seen =:status  WHERE thread  =:tid AND sender =:sender")
    fun updateSeenOfThread(tid: String?, sender: String?, status: String?)

    @Query("UPDATE Chats SET seen =:status  WHERE thread  =:tid AND sender =:sender AND seen= '0' ")
    fun updateSeenRecivedThread(tid: String?, sender: String?, status: String?)

    @Query("DELETE  FROM Chats WHERE chat_id =:id ")
    fun delete(id: String?)

    @Query("DELETE FROM Chats")
    fun deleteAll()

    @get:Query("SELECT * FROM Chats ORDER BY RID")
    val all: PagingSource<Int, Chats>?

    @Query("SELECT * FROM Chats WHERE chat_id = :id")
    fun selectChat(id: String?): Chats?


    //todo  paging3
    @Query("SELECT * FROM Chats WHERE thread =:thread order by createdAt ASC ")
    fun getMessageBy_paging( thread: String?): PagingSource<Int, Chats>

    // @Query("SELECT * FROM Chats WHERE thread = :Tid AND (type = 'I' OR type = 'V') ")
    @Query("SELECT * FROM Chats WHERE thread = :Tid AND (type = 'I' OR type = 'V')  ORDER BY CASE WHEN chat_id =:chat_id THEN 0 ELSE 1 END")
    fun selectAttachment(Tid: String?, chat_id: String?): PagingSource<Int, Chats>

    //without live and paged
    @Query("SELECT * FROM Chats WHERE thread = :Tid AND (type = 'I' OR type = 'V')  ORDER BY CASE WHEN chat_id =:chat_id THEN 0 ELSE 1 END")
    fun selectAttachmentNonPaged(Tid: String?, chat_id: String?): List<Chats>
}
