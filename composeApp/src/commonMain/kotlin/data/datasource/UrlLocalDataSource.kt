package data.datasource

import database.UrlDao
import database.UrlEntity

class UrlLocalDataSource(
    private val urlDao: UrlDao,
) {
    suspend fun getAllUrls(): List<UrlEntity> = urlDao.getAllUrls()

    suspend fun getUrl(id: Int): UrlEntity? = urlDao.getUrl(id = id)

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

    suspend fun deleteUrl(id: Int) {
        urlDao.deleteUrl(id = id)
    }

    suspend fun deleteAllUrl() {
        urlDao.deleteAllUrl()
    }
}
