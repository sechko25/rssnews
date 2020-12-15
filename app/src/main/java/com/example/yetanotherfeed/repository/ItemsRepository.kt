package com.example.yetanotherfeed.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.yetanotherfeed.database.ItemsDatabase
import com.example.yetanotherfeed.database.asDomainModel
import com.example.yetanotherfeed.models.Item
import com.example.yetanotherfeed.models.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ItemsRepository(private val database: ItemsDatabase) {

    val items: LiveData<List<Item>> = Transformations.map(database.itemDao.getAllItems()) {
        it.asDomainModel()
    }


    suspend fun refreshVideos(list: List<Item>) {
        withContext(Dispatchers.IO) {
            database.itemDao.clearData()
            database.itemDao.insertAll(list.asDatabaseModel())
        }
    }

    suspend fun updateEnclosuresWithFalse() {
        withContext(Dispatchers.IO) {
            database.itemDao.updateEnclosuresWithDefaults()
        }
    }
}