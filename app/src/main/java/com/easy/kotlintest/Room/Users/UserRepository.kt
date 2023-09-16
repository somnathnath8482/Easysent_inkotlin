package com.easy.kotlintest.Room.Users

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Interface.Messages.LiveData_Item
import com.easy.kotlintest.Interface.Messages.LiveData_List
import com.easy.kotlintest.Networking.Interface.OnDateSelect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserRepository {

    constructor(application: Application?) {
        val database = UserDatabase.getInstance(application!!)
        userDao = database!!.userDao()!!
        job = Job()
        scope = CoroutineScope(job)
    }

    private val userDao: UserDao
    private var job: Job
    private var scope: CoroutineScope

    fun insert(user: Users?) {
        scope.launch {
            userDao.insert(user)
        }
    }

    fun update(
        phone: String?,
        name: String?,
        profilePic: String?,
        gender: String?,
        about: String?,
        isVerifyed: String?,
        email: String?,
        token: String?
    ) {

        scope.launch {
            userDao.update(
                phone,
                name,
                profilePic,
                gender,
                about,
                isVerifyed,
                email,
                token
            )
        }
    }

    fun setIp(uid: String?, ip: String?) {
        scope.launch {
            if (ip?.length!! > 100) {
                userDao.setToken(uid, ip)
            } else {
                userDao.setIp(uid, ip)
            }
        }
    }

    fun getIp(uid: String?, onselect: OnDateSelect) {
        scope.launch {
            val ip = userDao.getIp(uid);
            onselect.OnSelect(ip ?: "");
        }
    }

    fun updateStatus(status: String?, id: String?) {
        scope.launch {
            userDao!!.updateStatus(status, id)
        }
    }

    fun delete(uid: String?) {
        scope.launch { userDao!!.delete(uid) }
    }

    fun deleteAll() {
        scope.launch { userDao.deleteAll() }
    }

    fun selectUser(uid: String?, item: Item<Users>) {
        scope.launch {
            item.onItem(userDao.selectUser(uid))
        }
    }

    fun selectUserLive(uid: String?, liveData_item: LiveData_Item<Users>) {
        scope.launch { liveData_item.onItem(userDao.selectUserlive(uid)) }
    }

    fun search(mail: String?, livedataMessages: LiveData_List<Users>) {

        scope.launch {
            // userDao.search(mail)


            val pager = Pager(
                PagingConfig(
                    pageSize = 3000,       // Count of items in one page
                    prefetchDistance = 9000, // Number of items to prefetch
                    enablePlaceholders = true, // Enable placeholders for data which is not yet loaded
                    initialLoadSize = 30000, // Count of items to be loaded initially
                    maxSize = PagingConfig.MAX_SIZE_UNBOUNDED // Count of total items to be shown in recyclerview
                )
            ) {
                userDao.search(mail)
            }

            livedataMessages.allMessage(pager.flow.cachedIn(scope).asLiveData())

        }


    }

    fun getAll(livedataMessages: LiveData_List<Users>) {

        scope.launch {
            // userDao.search(mail)


            val pager = Pager(
                PagingConfig(
                    pageSize = 3000,       // Count of items in one page
                    prefetchDistance = 9000, // Number of items to prefetch
                    enablePlaceholders = true, // Enable placeholders for data which is not yet loaded
                    initialLoadSize = 30000, // Count of items to be loaded initially
                    maxSize = PagingConfig.MAX_SIZE_UNBOUNDED // Count of total items to be shown in recyclerview
                )
            ) {
                userDao.getall
            }

            livedataMessages.allMessage(pager.flow.cachedIn(scope).asLiveData())

        }


    }


}
