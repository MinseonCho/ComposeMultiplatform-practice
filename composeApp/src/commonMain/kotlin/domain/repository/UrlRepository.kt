package domain.repository

import domain.model.Url

interface UrlRepository {
    suspend fun getAllUrls(): List<Url>
    suspend fun insertUrl(
        url: String,
        memo: String?,
    )
}
