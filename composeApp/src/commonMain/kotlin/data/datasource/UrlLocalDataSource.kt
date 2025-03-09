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

    suspend fun deleteUrls(indices: List<Int>) {
        urlDao.deleteUrls(indices = indices)
    }

    suspend fun deleteAllUrl() {
        urlDao.deleteAllUrl()
    }
}
