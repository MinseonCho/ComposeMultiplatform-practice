package data.repository

import data.datasource.UrlLocalDataSource
import domain.model.UrlInfo
import domain.repository.UrlRepository

class UrlRepositoryImpl(
    private val localDataSource: UrlLocalDataSource,
) : UrlRepository {
    override suspend fun getAllUrls(): List<UrlInfo> {
        return localDataSource.getAllUrls().map { entity ->
            UrlInfo(
                id = entity.id,
                url = entity.url,
                memo = entity.memo.orEmpty(),
                timestamp = entity.timestamp
            )
        }
    }

    override suspend fun insertUrl(
        url: String,
        memo: String?,
    ) {
        localDataSource.insertUrl(
            url = url,
            memo = memo,
        )
    }
}
