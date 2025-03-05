package domain.model

data class Url(
    val id: Long = 0,
    val url: String,
    val memo: String,
    val timestamp: Long,
)
