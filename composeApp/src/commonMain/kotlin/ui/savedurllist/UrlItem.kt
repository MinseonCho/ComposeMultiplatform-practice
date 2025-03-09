package ui.savedurllist

import domain.model.UrlInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UrlItem(
    val orderIndex: Int,
    val id: Int,
    val url: String,
    val memo: String,
    val savedTime: String,
)

fun List<UrlInfo>.toUrlItemList(): List<UrlItem> {
    return this.mapIndexed { index, urlInfo ->
        UrlItem(
            orderIndex = index,
            id = urlInfo.id.toInt(),
            url = urlInfo.url,
            memo = urlInfo.memo,
            savedTime = urlInfo.timestamp.formatCurrentTimeMillis()
        )
    }
}

private fun Long.formatCurrentTimeMillis(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat("yyyy년 M월 d일 HH:mm", Locale.KOREA)
    return dateFormat.format(date)
}
