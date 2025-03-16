package ui.page

sealed interface PageEvent {

    data class TriggerUrl(
        val url: String,
    ) : PageEvent

    data class ShowSnackBar(
        val message: String,
        val actionLabel: String? = null,
        val onDismissed: (() -> Unit)? = null,
        val onActionPerformed: (() -> Unit)? = null,
    ) : PageEvent

    data class CopyUrlToClipboard(
        val url: String,
    ) : PageEvent
}
