package domain.usecase

import domain.model.UrlInfo
import domain.repository.UrlRepository

class GetUrlHistoryUseCase(
    private val repository: UrlRepository,
) {
    suspend operator fun invoke(): List<UrlInfo> = repository.getAllUrls()
}
