package ui.page

sealed interface PageEvent {

    data class TriggerUrl(
        val url: String,
    ) : PageEvent

    data class ShowSnackBar(
        val message: String,
    ) : PageEvent
}
