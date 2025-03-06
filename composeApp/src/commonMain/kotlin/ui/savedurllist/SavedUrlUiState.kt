package ui.savedurllist

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SavedUrlUiState(
    val urls: ImmutableList<UrlItem> = persistentListOf(),
)
