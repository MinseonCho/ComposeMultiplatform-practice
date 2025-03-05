package data.repository

import data.datasource.UrlLocalDataSource
import domain.model.Url
import domain.repository.UrlRepository

class UrlRepositoryImpl(
    private val localDataSource: UrlLocalDataSource
) : UrlRepository {
    override suspend fun getAllUrls(): List<Url> {
        return localDataSource.getAllUrls().map { entity ->
            Url(
                id = entity.id,
                url = entity.url,
                timestamp = entity.timestamp
            )
        }
    }

    override suspend fun insertUrl(url: String) {
        localDataSource.insertUrl(url)
    }
}
