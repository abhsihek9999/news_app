package com.android.newsapp.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.newsapp.data.db.entity.Article

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article:Article) : Long

    @Query("SELECT * FROM articles_table")
    fun getArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}