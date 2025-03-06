package ui.page

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import model.QueryItem

data class PageUiState(
    val url: String = "",
    val queries: ImmutableList<QueryItem> = persistentListOf(),
    val savedUrl: String = "",
) {
    val isUrlSaved: Boolean
        get() = savedUrl.isNotBlank()
                && url.isNotBlank()
                && (url == savedUrl)
}
