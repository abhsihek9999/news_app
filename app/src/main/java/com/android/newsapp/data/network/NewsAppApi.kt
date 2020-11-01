package com.android.newsapp.data.network

import com.android.newsapp.data.network.response.BreakingNewsResponse
import com.android.newsapp.utils.Utils.Companion.API_KEY
import com.android.newsapp.utils.Utils.Companion.GET_BREAKING_NEWS
import com.android.newsapp.utils.Utils.Companion.SEARCH
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAppApi{
    //http://newsapi.org/v2/top-headlines?country=us&apiKey=de78ef52278b4d2ea9125cf154e837a0

    @GET(GET_BREAKING_NEWS)
   suspend fun getBreakingNews(
        @Query("country") country : String = "IND",
        @Query("page") page : Int,
        @Query("apikey") apikey : String = API_KEY
    ) : Response<BreakingNewsResponse>

    @GET(SEARCH)
    suspend fun search(
        @Query("q") searchQuery:String,
        @Query("page") page : Int,
        @Query("apikey") apikey :String = API_KEY
    ) : Response<BreakingNewsResponse>

}