package com.android.newsapp.data.db

import android.content.Context
import androidx.room.*
import com.android.newsapp.data.db.dao.NewsDao
import com.android.newsapp.data.db.entity.Article
import com.android.newsapp.utils.Converters


@Database(
    version = 1,
    entities = [Article::class]
)
@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao() : NewsDao

    companion object {

        private val instance : NewsDatabase ?= null
        private val LOCK = Any()

        operator fun invoke(context:Context) = instance ?: synchronized(LOCK){

            instance ?: createDb(context)
        }

        private fun createDb(context: Context) =

            Room.databaseBuilder(
                context.applicationContext,NewsDatabase::class.java
                ,"news_database"
            ).build()

        }

}