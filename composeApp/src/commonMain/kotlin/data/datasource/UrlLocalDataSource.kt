package data.datasource

import database.UrlDao
import database.UrlEntity

class UrlLocalDataSource(
    private val urlDao: UrlDao,
) {
    suspend fun getAllUrls(): List<UrlEntity> = urlDao.getAllUrls()

    suspend fun insertUrl(
        url: String,
        memo: String?,
    ) {
        urlDao.insertUrl(
            UrlEntity(
                url = url,
                memo = memo,
            )
        )
    }
}
