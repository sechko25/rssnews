package com.example.yetanotherfeed.models

import com.example.yetanotherfeed.database.DatabaseItem

data class Item (
    val title: String,
    val pubDate: String,
    val link: String,
    val guid: String,
    val author: String,
    val thumbnail: String,
    val description: String,
    val content: String,
    var enclosure: Any,
    val categories: List<String>
)


fun List<Item>.asDatabaseModel(): List<DatabaseItem> {
    return map {
        DatabaseItem(
            guid = it.guid,
            title = it.title,
            pubDate = it.pubDate,
            content = Regex("\\<.*?\\>").replace(it.content, ""),
            link = it.link,
            thumbnail = it.thumbnail,
            enclosure = false
        )
    }
}