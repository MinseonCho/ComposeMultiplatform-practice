package ui.savedurllist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.DeleteSavedAllUrl
import domain.usecase.DeleteSavedUrls
import domain.usecase.GetSavedUrlList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedUrlViewModel(
    private val getSavedUrlList: GetSavedUrlList,
    private val deleteSavedUrls: DeleteSavedUrls,
    private val deleteSavedAllUrl: DeleteSavedAllUrl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUrlUiState())
    val uiState: StateFlow<SavedUrlUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SavedUrlEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<SavedUrlEvent> = _eventChannel.receiveAsFlow()

    private val urlMap = sortedMapOf<Int, UrlItem>() // Map<orderIndex, UrlItem>
    private val pendingDeleteUrls = mutableMapOf<Int, UrlItem>() // Map<orderIndex, UrlItem>

    init {
        initSavedUrlList()
    }

    private fun initSavedUrlList() {
        viewModelScope.launch(Dispatchers.IO) {
            urlMap.clear()
            pendingDeleteUrls.clear()
            when (val response = getSavedUrlList()) {
                is GetSavedUrlList.Response.Success -> {
                    urlMap.putAll(
                        response.urls.toUrlItemList().associateBy { it.orderIndex }
                    )
                    updateUrlsUiState()
                }

                is GetSavedUrlList.Response.Failure -> {
                    showErrorSnackBar()
                }
            }
        }
    }

    fun onUrlClicked(urlItem: UrlItem) {
        _eventChannel.trySend(
            SavedUrlEvent.NavToUrlPage(
                id = urlItem.id
            )
        )
    }

    // uiState.urls 는 urlMap 의 values 로만 업데이트
    private fun updateUrlsUiState() {
        _uiState.update {
            it.copy(
                urls = urlMap.values.toImmutableList()
            )
        }
    }

    fun onDeleteButtonClicked(urlItem: UrlItem) {
        pendingDeleteUrls[urlItem.orderIndex] = urlItem

        // ui 에서 먼저 삭제
        urlMap.remove(urlItem.orderIndex)
        updateUrlsUiState()
        _eventChannel.trySend(
            SavedUrlEvent.ShowSnackBar(
                message = "삭제 완료",
                actionLabel = "실행 취소",
                onDismissed = {
                    deleteUrlItems(urlItems = listOf(urlItem))
                },
                onActionPerformed = {
                    pendingDeleteUrls.remove(urlItem.orderIndex)?.let {
                        urlMap[urlItem.orderIndex] = it
                    }
                    updateUrlsUiState()
                }
            )
        )
    }

    private fun deleteUrlItems(urlItems: List<UrlItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            when (deleteSavedUrls(indices = urlItems.map { it.id })) {
                DeleteSavedUrls.Response.Success -> Unit

                is DeleteSavedUrls.Response.Failure -> {
                    showErrorSnackBar()

                    // 복구
                    urlItems.forEach { item ->
                        pendingDeleteUrls.remove(item.orderIndex)?.let {
                            urlMap[it.orderIndex] = it
                        }
                    }
                    updateUrlsUiState()
                }
            }
        }
    }

    fun onDeleteAllButtonClicked() {
        pendingDeleteUrls.putAll(urlMap)

        val onRestoreRequired = {
            urlMap.putAll(pendingDeleteUrls)
            updateUrlsUiState()
            pendingDeleteUrls.clear()
        }

        // ui 에서 먼저 삭제
        urlMap.clear()
        updateUrlsUiState()
        _eventChannel.trySend(
            SavedUrlEvent.ShowSnackBar(
                message = "삭제 완료",
                actionLabel = "실행 취소",
                onDismissed = {
                    deleteAllUrlItems(
                        onRestoreRequired = onRestoreRequired
                    )
                },
                onActionPerformed = onRestoreRequired
            )
        )
    }

    private fun deleteAllUrlItems(
        onRestoreRequired: () -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when (deleteSavedAllUrl()) {
                DeleteSavedAllUrl.Response.Success -> Unit

                is DeleteSavedAllUrl.Response.Failure -> {
                    showErrorSnackBar()
                    onRestoreRequired()
                }
            }
        }
    }

    private fun showErrorSnackBar() {
        _eventChannel.trySend(
            SavedUrlEvent.ShowSnackBar(
                message = "알 수 없는 에러 발생 😣"
            )
        )
    }

    fun onStop() {
        if (pendingDeleteUrls.isNotEmpty()) {
            // 대기 중이던 삭제 예정 url 삭제
            viewModelScope.launch(Dispatchers.IO) {
                deleteUrlItems(
                    urlItems = pendingDeleteUrls.values.toList()
                )
            }
        }
    }
}
