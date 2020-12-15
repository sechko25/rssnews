package com.example.yetanotherfeed.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yetanotherfeed.models.Item


@Entity(tableName = "rss_feeds_items_table")
data class DatabaseItem(

    @PrimaryKey
    val guid: String,

    val pubDate: String,

    val title: String,

    val content: String,

    val link: String,

    val thumbnail: String,

    var enclosure: Boolean
)


fun List<DatabaseItem>.asDomainModel(): List<Item> {
    return map {
        Item (
            guid = it.guid,
            pubDate = it.pubDate,
            title = it.title,
            content = it.content,
            link = it.link,
            author = "",
            description = "",
            thumbnail = it.thumbnail,
            categories = ArrayList(),
            enclosure = it.enclosure
        )
    }
}