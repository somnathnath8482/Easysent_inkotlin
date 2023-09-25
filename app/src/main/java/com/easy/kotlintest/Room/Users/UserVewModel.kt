package com.easy.kotlintest.Room.Users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.easy.kotlintest.Interface.Messages.Item
import com.easy.kotlintest.Interface.Messages.LiveData_Item
import com.easy.kotlintest.Interface.Messages.LiveData_List
import com.easy.kotlintest.Networking.Interface.OnDateSelect


class UserVewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        repository = UserRepository(application)
    }

    fun insert(user: Users?) {
        repository.insert(user)
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
        repository.update(phone, name, profilePic, gender, about, isVerifyed, email, token)
    }

    fun updateStatus(status: String?, id: String?) {
        repository.updateStatus(status, id)
    }

    fun delete(user: String?) {
        repository.delete(user)
    }

    fun deleteAll() {
        repository.deleteAll()
    }

    fun setIp(uid: String?, ip: String?) {
        repository.setIp(uid, ip)
    }

    fun getIp(uid: String?, onDateSelect: OnDateSelect) {
        repository.getIp(uid, onDateSelect)
    }

    fun selectUser(uid: String?, item: Item<Users>) {
         repository.selectUser(uid, item)
    }

    fun selectUserLive(uid: String?, liveData_item: LiveData_Item<Users>) {
        repository.selectUserLive(uid, liveData_item)
    }

    fun search(Name_Or_Email: String?, livedataMessages: LiveData_List<Users>) {
        repository.search(Name_Or_Email, livedataMessages)
    }

    fun getAll(livedataMessages: LiveData_List<Users>) {
        return repository.getAll(livedataMessages)
    }
}
