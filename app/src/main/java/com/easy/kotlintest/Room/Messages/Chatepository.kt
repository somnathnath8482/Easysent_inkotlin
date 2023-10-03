package com.easy.kotlintest.Room.Messages

import android.app.Application
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Interface.Messages.ListMessages
import com.easy.kotlintest.Interface.Messages.LiveData_List
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class Chatepository{
    private val Chat_dao: ChatDao
    private var job: Job
    private var scope: CoroutineScope

    constructor(application: Application?)  {
        val database = MessageDataBase.getInstance(application!!)
        Chat_dao = database!!.chatDao()
        job = Job()
        scope = CoroutineScope(job)
    }

    fun insert(user: Chats?) {
        scope.launch { Chat_dao.insert(user) }

    }

    fun updateSeen(status: String?, id: String?) {

        scope.launch {

            Chat_dao.updateSeen(status, id)

        }
    }

    fun updateDelete(id: String?, value: String?) {

        scope.launch {

            Chat_dao.updateDeleted(id, value)

        }

    }

    fun updateSeenByThread(thread: String?, sender: String?, status: String?) {

        scope.launch {

            Chat_dao.updateSeenOfThread(thread, sender, status)

        }

    }

    fun updateSeenRecivedThread(thread: String?, sender: String?, status: String?) {

        scope.launch {
            Chat_dao.updateSeenRecivedThread(thread, sender, status)
        }
    }

    fun delete(uid: String?) {

        scope.launch {
            Chat_dao.delete(uid)

        }
    }

    fun deleteAll() {
        scope.launch { Chat_dao.deleteAll() }
    }

    fun selectChat(uid: String?, item: Item<Chats>) {
        scope.launch {

            item.onItem(Chat_dao.selectChat(uid))
        }


    }

    fun selectAttachment(uid: String?, chat_id: String?, list: LiveData_List<Chats>) {

        scope.launch {

            val pager = Pager(
                PagingConfig(
                    pageSize = 30,       // Count of items in one page
                    prefetchDistance = 90, // Number of items to prefetch
                    enablePlaceholders = true, // Enable placeholders for data which is not yet loaded
                    initialLoadSize = 30, // Count of items to be loaded initially
                    maxSize = PagingConfig.MAX_SIZE_UNBOUNDED // Count of total items to be shown in recyclerview
                )
            ) {
                Chat_dao.selectAttachment(uid, chat_id)
            }

            list.allMessage(pager.flow.cachedIn(scope).asLiveData())

        }

    }

    fun selectAttachmentNonPageed(uid: String?, chat_id: String?, list: ListMessages<Chats>) {

        scope.launch {

            list.allMessage(Chat_dao.selectAttachmentNonPaged(uid, chat_id))

        }

    }

    fun getChatByPaging(
        user: String?, me: String?, list: LiveData_List<Chats>
    ) {
        scope.launch {

            val pager = Pager(
               /* PagingConfig(
                    30,  //  Count of items in one page
                    30,  //  Number of items to prefetch
                    true,  // Enable placeholders for data which is not yet loaded
                    120,  // initialLoadSize - Count of items to be loaded initially
                    PagingConfig.MAX_SIZE_UNBOUNDED
                )*/
            PagingConfig(pageSize = 40)
            ) {
                Chat_dao.getMessageBy_paging(user, me)
            }

            list.allMessage(pager.flow.cachedIn(scope).asLiveData())

        }

    }

}
