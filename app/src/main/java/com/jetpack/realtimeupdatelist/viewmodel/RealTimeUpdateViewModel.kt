package com.jetpack.realtimeupdatelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.realtimeupdatelist.model.RealTimeUpdateItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class RealTimeUpdateViewModel: ViewModel() {
    private val _realTimeUpdateItem = MutableStateFlow(listOf<RealTimeUpdateItem>())
    val realTimeUpdateItem: StateFlow<List<RealTimeUpdateItem>> get() = _realTimeUpdateItem

    private val downloadQueue: MutableMap<Int, Flow<Int>> = mutableMapOf()

    init {
        getRealTimeUpdateItem()
    }

    private fun getRealTimeUpdateItem() {
        viewModelScope.launch(Dispatchers.Default) {
            val initialRealTimeUpdateItem = arrayListOf<RealTimeUpdateItem>()
            repeat(20) {
                initialRealTimeUpdateItem += RealTimeUpdateItem(
                    id = it + 1,
                    title = "Download File ${it + 1}",
                    downloadProgress = 0
                )
            }
            _realTimeUpdateItem.emit(initialRealTimeUpdateItem)
        }
    }

    private fun provideDownloadFlow(RealTimeUpdateItemId: Int): Flow<Int> {
        return flow {
            var progress = 10
            emit(progress)
            repeat(100) {
                progress += Random.nextInt(10, 25)
                delay(500L)
                if (progress >= 100)
                    emit(100)
                else
                    emit(progress)
                if (progress >= 100) {
                    downloadQueue.remove(RealTimeUpdateItemId)
                    return@flow
                }
            }
        }
    }

    fun onDownloadRealTimeUpdateItemClicked(RealTimeUpdateItemId: Int, index: Int) {
        if (downloadQueue.containsKey(RealTimeUpdateItemId))
            return
        val download: Flow<Int> = provideDownloadFlow(RealTimeUpdateItemId)
        downloadQueue[RealTimeUpdateItemId] = download
        observeDownload(index, download)
    }

    private fun observeDownload(index: Int, download: Flow<Int>) {
        viewModelScope.launch(Dispatchers.Default) {
            download.collect { progress ->
                val updatedRealTimeUpdateItem = _realTimeUpdateItem.value[index].copy(downloadProgress = progress)
                val mutableRealTimeUpdateItem = _realTimeUpdateItem.value.toMutableList()
                mutableRealTimeUpdateItem[index] = updatedRealTimeUpdateItem
                _realTimeUpdateItem.value = mutableRealTimeUpdateItem.toList()
            }
        }
    }
}






















