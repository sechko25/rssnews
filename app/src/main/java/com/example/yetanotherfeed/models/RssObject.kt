package com.example.yetanotherfeed.models

data class RssObject(
    val status: String,
    val feed: Feed,
    val items: List<Item>
)