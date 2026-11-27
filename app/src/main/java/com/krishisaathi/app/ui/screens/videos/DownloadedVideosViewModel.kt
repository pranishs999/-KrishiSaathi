package com.krishisaathi.app.ui.screens.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.data.remote.AppSyncController
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import com.krishisaathi.app.media.VideoDownloadManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DownloadedVideoItem(
    val video: EducationalVideo,
    val cropNameNe: String
)

data class DownloadedVideosUiState(
    val videos: List<DownloadedVideoItem> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
)

class DownloadedVideosViewModel(
    private val repository: BakaiyaCropRepository,
    private val downloadManager: VideoDownloadManager,
    private val appSyncController: AppSyncController? = null
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<DownloadedVideosUiState> = combine(
        repository.getDownloadedVideos(),
        repository.getCrops(),
        _isRefreshing
    ) { downloadedVideos, crops, isRefreshing ->
        val cropMap = crops.associateBy { it.id }
        val items = downloadedVideos.map { video ->
            DownloadedVideoItem(
                video = video,
                cropNameNe = cropMap[video.cropId]?.nameNe ?: "कृषि बाली"
            )
        }
        DownloadedVideosUiState(videos = items, isLoading = false, isRefreshing = isRefreshing)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DownloadedVideosUiState(isLoading = true)
    )

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                if (appSyncController != null) {
                    appSyncController.refreshNow(force = true)
                } else {
                    repository.syncRemoteContent()
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun deleteVideo(videoId: String) {
        downloadManager.deleteVideo(videoId)
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DownloadedVideosViewModel(
                    repository = appContainer.bakaiyaCropRepository,
                    downloadManager = appContainer.videoDownloadManager,
                    appSyncController = appContainer.appSyncController
                )
            }
        }
    }
}
