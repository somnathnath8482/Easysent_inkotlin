package com.easy.kotlintest.Room.Users

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.easy.kotlintest.Room.Thread.Message_Thread
import com.easy.kotlintest.Room.Thread.Thread_dao


@Database(
    entities = [Users::class, Message_Thread::class],
    version = 2,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao?
    abstract fun thread_dao(): Thread_dao?
    //abstract fun blockDao(): BlockDao?

    companion object {
        private  var instance: UserDatabase? = null

        @Synchronized
        fun getInstance(context: Context): UserDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java, "User_databade")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}
