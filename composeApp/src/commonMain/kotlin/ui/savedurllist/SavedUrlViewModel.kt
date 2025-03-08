package ui.savedurllist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.DeleteSavedAllUrl
import domain.usecase.DeleteSavedUrl
import domain.usecase.GetSavedUrlList
import kotlinx.collections.immutable.persistentListOf
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
    private val deleteSavedUrl: DeleteSavedUrl,
    private val deleteSavedAllUrl: DeleteSavedAllUrl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUrlUiState())
    val uiState: StateFlow<SavedUrlUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SavedUrlEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<SavedUrlEvent> = _eventChannel.receiveAsFlow()

    init {
        initSavedUrlList()
    }

    private fun initSavedUrlList() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = getSavedUrlList()) {
                is GetSavedUrlList.Response.Success -> {
                    _uiState.update {
                        it.copy(
                            urls = response.urls.map {
                                it.toUrlItem()
                            }.toImmutableList()
                        )
                    }
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

    fun onDeleteButtonClicked(urlItem: UrlItem) {
        viewModelScope.launch(Dispatchers.IO) {
            when (deleteSavedUrl(id = urlItem.id)) {
                DeleteSavedUrl.Response.Success -> {
                    _uiState.update {
                        it.copy(
                            urls = _uiState.value.urls.toMutableList().apply {
                                remove(urlItem)
                            }.toImmutableList()
                        )
                    }
                }

                is DeleteSavedUrl.Response.Failure -> {
                    showErrorSnackBar()
                }
            }
        }
    }

    fun onDeleteAllButtonClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            when (deleteSavedAllUrl()) {
                DeleteSavedAllUrl.Response.Success -> {
                    _uiState.update {
                        it.copy(
                            urls = persistentListOf()
                        )
                    }
                }

                is DeleteSavedAllUrl.Response.Failure -> {
                    showErrorSnackBar()
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
}
