package com.easy.kotlintest.Room.Thread

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Interface.Messages.LiveData_List

class Thread_ViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Thread_repository

    init {
        repository = Thread_repository(application)
    }

    fun insert(user: Message_Thread?) {
        repository.insert(user)
    }

    fun updateLastSeen(
        message: String?,
        status: String?,
        type: String?,
        id: String?,
        time: String?
    ) {
        repository.update_last_seen(message, status, type, id, time)
    }

    fun delete(user: String?) {
        repository.delete(user)
    }

    fun updateUnread(thread_id: String?) {
        repository.UpdateUnread(thread_id)
    }

    fun deleteAll() {
        repository.deleteAll()
    }

    fun selectThread(uid: String?,listener: Item<Message_Thread>){
        return repository.selectThread(uid,listener)
    }

    fun getActiveThreds(id: String?, live_data: LiveData_List<Active_Thread>) {
        repository.getActiveThread(id, live_data)
    }

    fun getAll( live_data: LiveData_List<Message_Thread>) {
        repository.getAll(live_data)
    }
}
