package ui.savedurllist

sealed interface SavedUrlEvent {

    data class NavToUrlPage(
        val urlItem: UrlItem,
    ) : SavedUrlEvent
}
