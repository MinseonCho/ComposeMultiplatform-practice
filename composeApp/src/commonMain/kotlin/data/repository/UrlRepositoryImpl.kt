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

    override suspend fun getUrl(id: Int): UrlInfo? {
        return localDataSource.getUrl(id = id)?.let {
            UrlInfo(
                id = it.id,
                url = it.url,
                memo = it.memo.orEmpty(),
                timestamp = it.timestamp
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

    override suspend fun deleteUrls(indices: List<Int>) {
        localDataSource.deleteUrls(indices = indices)
    }

    override suspend fun deleteAllUrl() {
        localDataSource.deleteAllUrl()
    }
}
