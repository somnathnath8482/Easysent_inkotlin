package com.easy.kotlintest.Room.Thread

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Interface.Messages.LiveData_Messages
import com.easy.kotlintest.Room.Users.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class Thread_repository {

    constructor(application: Application?) {
        val database = UserDatabase.getInstance(application!!)
        thread_dao = database!!.thread_dao()!!
        job = Job()
        scope = CoroutineScope(job)
    }

    private val thread_dao: Thread_dao
    private var job: Job
    private var scope: CoroutineScope

    fun insert(user: Message_Thread?) {
        scope.launch {
            thread_dao.insert(user)
        }
    }

    fun update_last_seen(
        message: String?,
        status: String?,
        type: String?,
        id: String?,
        time: String?
    ) {

        scope.launch {
            thread_dao.update_lastseen(message, status, type, id, time)
        }

    }

    fun delete(uid: String?) {
        scope.launch { thread_dao.delete(uid) }
    }

    fun UpdateUnread(uid: String?) {
        scope.launch { thread_dao.updateUnread(uid) }
    }

    fun deleteAll() {
        scope.launch { thread_dao.deleteAll() }
    }

    fun selectThread(uid: String?, item: Item<Message_Thread>) {
        scope.launch {
            item.onItem(thread_dao.selectthread(uid))
        }
    }

    fun getActiveThread(Name_Or_Email: String?, live_data: LiveData_Messages<Message_Thread>) {
        scope.launch {

            val pager = Pager(
                PagingConfig(
                    pageSize = 3000,       // Count of items in one page
                    prefetchDistance = 9000, // Number of items to prefetch
                    enablePlaceholders = true, // Enable placeholders for data which is not yet loaded
                    initialLoadSize = 30000, // Count of items to be loaded initially
                    maxSize = PagingConfig.MAX_SIZE_UNBOUNDED // Count of total items to be shown in recyclerview
                )
            ) {
                thread_dao.getActiveThread(Name_Or_Email)
            }

            live_data.allMessage(pager.flow.cachedIn(scope).asLiveData())


        }
    }

    fun getAll(live_data: LiveData_Messages<Message_Thread>) {
        scope.launch {
            val pager = Pager(
                PagingConfig(
                    pageSize = 3000,       // Count of items in one page
                    prefetchDistance = 9000, // Number of items to prefetch
                    enablePlaceholders = true, // Enable placeholders for data which is not yet loaded
                    initialLoadSize = 30000, // Count of items to be loaded initially
                    maxSize = PagingConfig.MAX_SIZE_UNBOUNDED // Count of total items to be shown in recyclerview
                )
            ) {
                thread_dao.all
            }

            live_data.allMessage(pager.flow.cachedIn(scope).asLiveData())
        }
    }

    //-------------------------//

}
