package domain.repository

import domain.model.UrlInfo

interface UrlRepository {
    suspend fun getAllUrls(): List<UrlInfo>

    suspend fun getUrl(id: Int): UrlInfo?

    suspend fun insertUrl(
        url: String,
        memo: String?,
    )

    suspend fun deleteUrls(indices: List<Int>)

    suspend fun deleteAllUrl()
}
