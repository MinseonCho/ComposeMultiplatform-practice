package ui.page

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import model.QueryItem

data class PageUiState(
    val url: String = "",
    val queries: ImmutableList<QueryItem> = persistentListOf(),
    val isUrlSaved: Boolean = false,
)
