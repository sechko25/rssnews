package com.example.yetanotherfeed.viewmodels

import android.app.Application
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yetanotherfeed.MainActivity
import com.example.yetanotherfeed.database.getDatabase
import com.example.yetanotherfeed.network.YetAnotherFeedNetwork
import com.example.yetanotherfeed.repository.ItemsRepository
import kotlinx.coroutines.*
import retrofit2.HttpException


enum class LoadingStatus { LOADING, ERROR, DONE }

class OverviewViewModel(application: Application) : ViewModel() {



    private val APP_PREFERENCES_LINK = "linkRss"
    private var linkRss: String

    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError


    private val _status = MutableLiveData<LoadingStatus>()
    val status: LiveData<LoadingStatus>
        get() = _status

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    private val itemsRepository = ItemsRepository(getDatabase(application))

    val items = itemsRepository.items


    init {
        linkRss = if (MainActivity.sharedPreferences.contains(APP_PREFERENCES_LINK))
            MainActivity.sharedPreferences.getString(APP_PREFERENCES_LINK, "")!!
        else
            ""
        refreshDataFromRepository(linkRss)
//        refreshDataFromRepository("https://news.tut.by/rss/economics.rs`s")
//        refreshDataFromRepository("http://feeds.bbci.co.uk/news/rss.xml")
//        refreshDataFromRepository("https://news.tut.by/rss.html")
    }

    fun refreshDataFromRepository(filter: String) {
        if (filter != "") {
            coroutineScope.launch {
                val getRssObjectDeferred = YetAnotherFeedNetwork.feeds.getFeeds(filter)
                try {
                    _status.value = LoadingStatus.LOADING
                    val objectResult = getRssObjectDeferred.await()
                    _eventNetworkError.value = false
                    _status.value = LoadingStatus.DONE
                    itemsRepository.refreshVideos(objectResult.items)
                    // Save linkRss to SharedPreferences
                    val editor = MainActivity.sharedPreferences.edit()
                    editor.putString(APP_PREFERENCES_LINK, filter)
                    editor.apply()
                } catch (e: HttpException) {
                    _status.value = LoadingStatus.ERROR
                    _eventNetworkError.value = true
                } catch (e: Exception) {
                    _status.value = LoadingStatus.ERROR
                    setDeafaultEnclosures()
                }
            }
        }
    }

    private fun setDeafaultEnclosures() {
        coroutineScope.launch {
            itemsRepository.updateEnclosuresWithFalse()
        }
    }

    fun instantiateEditTxtView(editText: EditText) {
        if (linkRss.isNotEmpty())
            editText.setText(linkRss)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OverviewViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}