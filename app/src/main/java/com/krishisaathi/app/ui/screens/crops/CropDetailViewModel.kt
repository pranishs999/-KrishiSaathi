package com.krishisaathi.app.ui.screens.crops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.krishisaathi.app.di.AppContainer
import com.krishisaathi.app.domain.model.BakaiyaCrop
import com.krishisaathi.app.domain.model.CropImage
import com.krishisaathi.app.domain.model.CropVarietyRequirement
import com.krishisaathi.app.domain.model.EducationalVideo
import com.krishisaathi.app.domain.repository.BakaiyaCropRepository
import com.krishisaathi.app.media.VideoDownloadManager
import com.krishisaathi.app.media.VideoDownloadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CropDetailUiState(
    val crop: BakaiyaCrop? = null,
    val varietyRequirements: List<CropVarietyRequirement> = emptyList(),
    val selectedVarietyId: String? = null,
    val images: List<CropImage> = emptyList(),
    val videos: List<EducationalVideo> = emptyList(),
    val selectedTopicId: Int? = null,
    val downloadStates: Map<String, VideoDownloadState> = emptyMap(),
    val fullScreenImage: CropImage? = null,
    val launchErrorMessage: String? = null,
    val isLoading: Boolean = true
)

class CropDetailViewModel(
    private val cropId: String,
    private val repository: BakaiyaCropRepository,
    private val downloadManager: VideoDownloadManager
) : ViewModel() {

    private val _selectedTopicId = MutableStateFlow<Int?>(null)
    private val _selectedVarietyId = MutableStateFlow<String?>(null)
    private val _fullScreenImage = MutableStateFlow<CropImage?>(null)
    private val _launchError = MutableStateFlow<String?>(null)

    private data class CropContentTuple(
        val crop: BakaiyaCrop?,
        val varietyRequirements: List<CropVarietyRequirement>,
        val images: List<CropImage>,
        val videos: List<EducationalVideo>,
        val downloadStates: Map<String, VideoDownloadState>
    )

    val uiState: StateFlow<CropDetailUiState> = combine(
        combine(
            repository.getCropById(cropId),
            repository.getVarietyRequirementsForCrop(cropId),
            repository.getCropImages(cropId),
            repository.getCropVideos(cropId),
            downloadManager.downloadStates
        ) { crop, varieties, images, videos, downloadStates ->
            CropContentTuple(crop, varieties, images, videos, downloadStates)
        },
        _selectedVarietyId,
        _selectedTopicId,
        _fullScreenImage,
        _launchError
    ) { content, selectedVarId, topicId, fullScreenImg, launchErr ->
        val filteredVideos = if (topicId == null) {
            content.videos
        } else {
            content.videos.filter { it.topicId == topicId }
        }
        CropDetailUiState(
            crop = content.crop,
            varietyRequirements = content.varietyRequirements,
            selectedVarietyId = selectedVarId,
            images = content.images,
            videos = filteredVideos,
            selectedTopicId = topicId,
            downloadStates = content.downloadStates,
            fullScreenImage = fullScreenImg,
            launchErrorMessage = launchErr,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CropDetailUiState(isLoading = true)
    )

    fun selectVariety(varietyId: String?) {
        _selectedVarietyId.value = varietyId
    }

    fun selectTopic(topicId: Int?) {
        _selectedTopicId.value = topicId
    }

    fun openFullScreenImage(image: CropImage) {
        _fullScreenImage.value = image
    }

    fun closeFullScreenImage() {
        _fullScreenImage.value = null
    }

    fun setLaunchError(message: String?) {
        _launchError.value = message
    }

    fun startOrResumeDownload(video: EducationalVideo) {
        downloadManager.startOrResumeDownload(video)
    }

    fun pauseDownload(videoId: String) {
        downloadManager.pauseDownload(videoId)
    }

    fun deleteDownloadedVideo(videoId: String) {
        downloadManager.deleteVideo(videoId)
    }

    companion object {
        fun factory(cropId: String, appContainer: AppContainer): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CropDetailViewModel(
                        cropId = cropId,
                        repository = appContainer.bakaiyaCropRepository,
                        downloadManager = appContainer.videoDownloadManager
                    )
                }
            }
    }
}
