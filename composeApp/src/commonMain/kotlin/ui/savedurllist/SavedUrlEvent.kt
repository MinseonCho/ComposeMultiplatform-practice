package ui.savedurllist

sealed interface SavedUrlEvent {

    data class NavToUrlPage(
        val id: Int,
    ) : SavedUrlEvent
}
