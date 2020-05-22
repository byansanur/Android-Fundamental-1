package com.byandev.submission2uiux.network

import com.byandev.submission2uiux.utils.Constants.Companion.BASE_URL
import com.byandev.submission2uiux.utils.Constants.Companion.TOKEN_KEY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient {

    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor {chain ->
                    val tokenKey = TOKEN_KEY
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("Authorization", tokenKey)
                        .build()
                    chain.proceed(request)
                }
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        val api by lazy {
            retrofit.create(ApiEndpoint::class.java)
        }
    }


}