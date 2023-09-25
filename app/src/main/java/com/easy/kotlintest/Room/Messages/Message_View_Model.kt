package com.easy.kotlintest.Room.Messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Interface.Messages.LiveData_List


class Message_View_Model(application: Application) : AndroidViewModel(application) {
    private val repository: Chatepository

    init {
        repository = Chatepository(application)

    }

    fun insert(chat: Chats?) {
        repository.insert(chat)
    }


    fun updateSeen(status: String?, id: String?) {
        repository.updateSeen(status,id)
    }

    fun updateDelete(id: String?, value: String?) {
        repository.updateDelete(id, value)
    }

    fun updateSeenByThread(thread: String?, sender: String?, status: String?) {
        repository.updateSeenByThread(thread, sender, status)
    }

    fun updateSeenRecivedThread(thread: String?, sender: String?, status: String?) {
        repository.updateSeenRecivedThread(thread, sender, status)
    }

    fun delete(chat_id: String?) {
        repository.delete(chat_id)
    }

    fun deleteAll() {
        repository.deleteAll()
    }

    fun selectChat(uid: String?,item: Item<Chats>) {
        repository.selectChat(uid,item)
    }


    fun selectAttachment(uid: String?, chat_id: String?, list: LiveData_List<Chats>) {
        return repository.selectAttachment(uid, chat_id,list)
    }


    fun getChat_By_Paged(
        user: String?,
        me: String?,
        list: LiveData_List<Chats>
    ) {
        repository.getChatByPaging(user, me,  list)
    }
}
