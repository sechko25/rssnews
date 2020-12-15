package com.example.yetanotherfeed.network

import com.example.yetanotherfeed.models.RssObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface RssFeedService {
    @GET("api.json")
    fun getFeeds(@Query("rss_url") type: String):
            Deferred<RssObject>
}


object YetAnotherFeedNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit
        get() = Retrofit.Builder()
            .baseUrl("https://api.rss2json.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    val feeds = retrofit.create(RssFeedService::class.java)
}