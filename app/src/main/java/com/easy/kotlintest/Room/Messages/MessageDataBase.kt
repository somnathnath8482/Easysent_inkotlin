package com.easy.kotlintest.Room.Messages

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Chats::class], version = 2, exportSchema = false)
abstract class MessageDataBase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        private var instance: MessageDataBase? = null
        @Synchronized
        fun getInstance(context: Context): MessageDataBase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    MessageDataBase::class.java, "Message_databade"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}
