package com.example.yetanotherfeed.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDAO {

    @Query("SELECT * FROM rss_feeds_items_table")
    fun getAllItems(): LiveData<List<DatabaseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<DatabaseItem>)

    @Query("DELETE FROM rss_feeds_items_table")
    fun clearData()

    @Update
    fun update(item: DatabaseItem)

    @Query("SELECT * FROM rss_feeds_items_table WHERE link = :link")
    fun getItemByLink(link: String) : DatabaseItem

    @Query("UPDATE rss_feeds_items_table SET enclosure = 0")
    fun updateEnclosuresWithDefaults()
}

@Database(entities = [DatabaseItem::class], version = 1)
abstract class ItemsDatabase : RoomDatabase() {
    abstract val itemDao: ItemDAO
}


private lateinit var INSTANCE: ItemsDatabase


fun getDatabase(context: Context): ItemsDatabase {
    synchronized(ItemsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ItemsDatabase::class.java,
                "yet_another_feed"
            ).build()
        }
    }
    return INSTANCE
}