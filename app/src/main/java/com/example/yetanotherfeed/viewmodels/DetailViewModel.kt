package com.example.yetanotherfeed.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yetanotherfeed.database.DatabaseItem
import com.example.yetanotherfeed.database.ItemDAO
import kotlinx.coroutines.*

class DetailViewModel(
    val itemLink: String,
    val dataSource: ItemDAO
) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _item = MutableLiveData<DatabaseItem>()
    val item: LiveData<DatabaseItem>
        get() = _item

    private val _pageIsLoaded = MutableLiveData<Boolean>()
    val pageIsLoaded: LiveData<Boolean>
        get() = _pageIsLoaded


    init {
        _pageIsLoaded.value = true
        instantiateItem()
    }

    private fun instantiateItem() {
        coroutineScope.launch {
            _item.value = getItemByLink()
        }
    }

    private suspend fun getItemByLink(): DatabaseItem {
        return withContext(Dispatchers.IO) {
            dataSource.getItemByLink(itemLink)
        }
    }

    fun setCachedState() {
        if (pageIsLoaded.value!!)
            coroutineScope.launch {
                updateItem()
            }
    }

    private suspend fun updateItem() {
        withContext(Dispatchers.IO) {
            val newItem = item.value
            newItem?.enclosure = true
            dataSource.update(newItem!!)
        }
    }


    fun onError(){
        _pageIsLoaded.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(
        val itemLink: String,
        val dataSource: ItemDAO
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(itemLink, dataSource) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}