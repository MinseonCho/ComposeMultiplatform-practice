package ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.usecase.GetSavedUrl
import domain.usecase.SaveUrl
import io.ktor.http.URLBuilder
import io.ktor.http.decodeURLPart
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
import model.QueryItem

class PageViewModel(
    private val saveUrl: SaveUrl,
    private val getSavedUrl: GetSavedUrl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PageUiState())
    val uiState: StateFlow<PageUiState> = _uiState.asStateFlow()

    private var url: String = ""
    private val queryMap = sortedMapOf<Int, QueryItem>()

    private val _eventChannel = Channel<PageEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<PageEvent> = _eventChannel.receiveAsFlow()

    fun init(urlId: Int?) {
        if (urlId == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            when (val response = getSavedUrl(id = urlId)) {
                is GetSavedUrl.Response.Success -> {
                    val savedUrl = response.urlInfo.url

                    _uiState.update {
                        it.copy(
                            savedUrl = savedUrl
                        )
                    }
                    handleUrlUpdate(url = savedUrl)
                }

                GetSavedUrl.Response.NotExist,
                is GetSavedUrl.Response.Failure -> {
                    showErrorSnackBar()
                }
            }
        }
    }

    fun onUrlChanged(url: String) {
        handleUrlUpdate(url = url)
    }

    private fun handleUrlUpdate(url : String) {
        this.url = url
        _uiState.update {
            it.copy(url = this.url)
        }
        parseUrlQueriesWith(url = this.url)
    }

    fun onSendButtonClicked() {
        viewModelScope.launch {
            _eventChannel.send(PageEvent.TriggerUrl(url))
        }
    }

    private fun parseUrlQueriesWith(url: String) {
        queryMap.clear()
        queryMap.putAll(
            parseQueryParameters(url).associateBy { it.id }
        )
        putNewEmptyQuery()
        _uiState.update {
            it.copy(
                queries = queryMap.values.toImmutableList()
            )
        }
    }

    fun onCheckedChanged(
        queryItem: QueryItem,
        isChecked: Boolean,
    ) {
        val item = queryMap[queryItem.id] ?: return

        putNewQueryData(
            newQueryItem = item.copy(isChecked = isChecked)
        )
    }

    fun onQueryValueChanged(
        queryItem: QueryItem,
        newValue: String,
    ) {
        val item = queryMap[queryItem.id] ?: return

        putNewQueryData(
            newQueryItem = item.copy(
                value = newValue,
                isChecked = true
            )
        )
    }

    fun onQueryKeyChanged(
        queryItem: QueryItem,
        newKey: String,
    ) {
        val item = queryMap[queryItem.id] ?: return

        putNewQueryData(
            newQueryItem = item.copy(
                key = newKey,
                isChecked = true
            )
        )
    }

    private fun putNewQueryData(newQueryItem: QueryItem) {
        queryMap[newQueryItem.id] = newQueryItem

        if (newQueryItem.id == queryMap.lastKey()) {
            putNewEmptyQuery()
        }

        url = generateUrlStringWith(
            originUrl = url,
            newQueries = queryMap.values.toList()
        )
        _uiState.update {
            it.copy(
                url = url.decodeURLPart(), // ui state 에는 decode 된 url 노출
                queries = queryMap.values.toImmutableList()
            )
        }
    }

    private fun putNewEmptyQuery() {
        val queryId = if (queryMap.isEmpty()) {
            0
        } else {
            queryMap.lastKey() + 1
        }
        val newItem = QueryItem.generateEmptyQueryItem( id = queryId)
        queryMap[newItem.id] = newItem
    }

    private fun generateUrlStringWith(
        originUrl: String,
        newQueries: List<QueryItem>,
    ): String {
        val newUrlBuilder = URLBuilder(originUrl)

        newUrlBuilder.parameters.clear()
        newQueries
            .filter { it.isChecked }
            .forEach { query ->
                newUrlBuilder.parameters.append(query.key, query.value)
            }
        return newUrlBuilder.build().toString()
    }

    private fun parseQueryParameters(url: String): List<QueryItem> {
        val queryStartIndex = url.indexOf("?")

        if (queryStartIndex == -1 || queryStartIndex == url.lastIndex) {
            return emptyList()
        }

        val fragmentIndex = url.indexOf("#")
        val queryString = when (fragmentIndex == -1) {
            true -> {
                url.substring(queryStartIndex + 1)
            }

            false -> {
                url.substring(queryStartIndex + 1, fragmentIndex)
            }
        }

        return queryString.split("&")
            .mapIndexed { index, param ->
                val parts = param.split("=")
                val key = parts.getOrNull(0).orEmpty()
                val value = parts.getOrNull(1).orEmpty()

                QueryItem(
                    id = index,
                    key = key,
                    value = value
                )
            }
    }

    fun onSaveButtonClicked() {
        if (url.isBlank()) {
            showSnackBar(message = "저장할 URL이 없어요.😡")
            return
        }

        if (_uiState.value.isUrlSaved) {
            showSnackBar(message = "이미 저장된 URL 입니다.⭐")
        } else {
            saveUrl()
        }
    }

    private fun saveUrl() {
        viewModelScope.launch(Dispatchers.IO) {
            saveUrl.invoke(
                url = url,
                memo = null
            )
            _uiState.update {
                it.copy(
                    savedUrl = url
                )
            }
            showSnackBar(message = "저장 완료")
        }
    }

    fun onCopyButtonClicked() {
        if (url.isBlank()) {
            showSnackBar(message = "복사할 URL이 없어요.😡")
            return
        }

        _eventChannel.trySend(
            PageEvent.CopyUrlToClipboard(
                url = url
            )
        )
        showSnackBar(message = "클립보드에 복사 완료")
    }

    private fun showErrorSnackBar() {
        showSnackBar(
            message = "에러가 발생했습니다."
        )
    }

    private fun showSnackBar(message: String) {
        _eventChannel.trySend(
            PageEvent.ShowSnackBar(
                message = message
            )
        )
    }
}
