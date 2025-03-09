package ui.savedurllist

sealed interface SavedUrlEvent {

    data class NavToUrlPage(
        val id: Int,
    ) : SavedUrlEvent

    data class ShowSnackBar(
        val message: String,
        val actionLabel: String? = null,
        val onDismissed: (() -> Unit)? = null,
        val onActionPerformed: (() -> Unit)? = null,
    ) : SavedUrlEvent
}
