package com.seif.newsappmvvm.api

import com.seif.newsappmvvm.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitInstance {
    companion object{
        // this lazy means that we only initialize this here once.
        private val retrofit by lazy {
            // we just attached this to our retrofit object to be able to see which request
            // we are actually making and what the responses are.
            val logging = HttpLoggingInterceptor()
            // to see the body of our response
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            // so we use that interceptor to create a network client
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            // we can use that client to pass it to our retrofit instance
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        // this is the api object which we will be able to use it from every where to make network request.
        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}