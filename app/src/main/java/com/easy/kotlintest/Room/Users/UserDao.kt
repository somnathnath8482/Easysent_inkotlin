package com.easy.kotlintest.Room.Users

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {
    @Insert
    fun insert(user: Users?)

    @Query("UPDATE Users  set phone =:phone, name =:name, profilePic =:profilePic, gender =:gender,about =:about, verifyed =:isVerifyed,token =:token WHERE email =:email ")
    fun update(
        phone: String?,
        name: String?,
        profilePic: String?,
        gender: String?,
        about: String?,
        isVerifyed: String?,
        email: String?,
        token: String?
    )

    @Query("UPDATE Users  set fstatus =:status WHERE user_id =:id ")
    fun updateStatus(status: String?, id: String?)

    @Query("DELETE  FROM Users WHERE email =:email ")
    fun delete(email: String?)

    @Query("DELETE FROM Users")
    fun deleteAll()

    @Query("SELECT ip FROM USERS WHERE user_id=:uid")
    fun getIp(uid: String?): String?

    @Query("UPDATE USERS SET ip=:ip WHERE user_id=:uid")
    fun setIp(uid: String?, ip: String?)

    @Query("UPDATE USERS SET token =:token  WHERE user_id=:uid")
    fun setToken(uid: String?, token: String?)

    @get:Query("SELECT * FROM Users ORDER BY RID")
    val getall:  PagingSource<Int, Users>

    @Query("SELECT * FROM Users WHERE user_id = :email")
    fun selectUser(email: String?): Users?

    @Query("SELECT * FROM Users WHERE user_id = :uid")
    fun selectUserlive(uid: String?): LiveData<Users>

    @Query("SELECT * FROM Users WHERE name LIKE :name_or_Email || '%' OR email LIKE :name_or_Email || '%' ")
    fun search(name_or_Email: String?): PagingSource<Int, Users> //  select *  from Users  U   join message_thread T on u.user_id=T.id
}
