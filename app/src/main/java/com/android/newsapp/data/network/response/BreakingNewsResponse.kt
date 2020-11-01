package com.android.newsapp.data.network.response


import com.android.newsapp.data.db.entity.Article
import com.google.gson.annotations.SerializedName

data class BreakingNewsResponse(
    @SerializedName("articles")
    val articles: MutableList<Article>,
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int
)