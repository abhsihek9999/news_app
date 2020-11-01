package com.android.newsapp.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.newsapp.data.network.response.Source
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "articles_table")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @SerializedName("author")
    val author: String? = null,
    @SerializedName("content")
    val content: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("source")
    val source: Source,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlToImage")
    val urlToImage: String
): Serializable