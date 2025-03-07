package ui.savedurllist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUrlUiState())
    val uiState: StateFlow<SavedUrlUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SavedUrlEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<SavedUrlEvent> = _eventChannel.receiveAsFlow()

    init {
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
                    response.throwable.printStackTrace()
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

    }
}
