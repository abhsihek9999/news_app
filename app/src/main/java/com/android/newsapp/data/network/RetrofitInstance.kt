package com.android.newsapp.data.network

import com.android.newsapp.utils.Utils.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance{

    companion object{

        private val retrofit by lazy {
            var loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient
                .Builder()
                .addInterceptor(loggingInterceptor)
                .build()

             retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }

        val api by lazy {
            retrofit.create(NewsAppApi::class.java)
        }

    }


}