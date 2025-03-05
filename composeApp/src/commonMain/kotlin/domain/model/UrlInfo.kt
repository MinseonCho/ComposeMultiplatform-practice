package domain.model

data class UrlInfo(
    val id: Long = 0,
    val url: String,
    val memo: String,
    val timestamp: Long,
)
