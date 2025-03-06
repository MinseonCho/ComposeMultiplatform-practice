package ui.savedurllist

import domain.model.UrlInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UrlItem(
    val id: Int,
    val url: String,
    val memo: String,
    val savedTime: String,
)

fun UrlInfo.toUrlItem(): UrlItem {
    return UrlItem(
        id = id.toInt(),
        url = url,
        memo = memo,
        savedTime = timestamp.formatCurrentTimeMillis()
    )
}

private fun Long.formatCurrentTimeMillis(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat("yyyy년 M월 d일 HH:mm", Locale.KOREA)
    return dateFormat.format(date)
}
