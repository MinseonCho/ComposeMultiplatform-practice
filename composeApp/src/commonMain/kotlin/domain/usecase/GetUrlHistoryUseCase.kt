package domain.usecase

import domain.model.Url
import domain.repository.UrlRepository

class GetUrlHistoryUseCase(
    private val repository: UrlRepository,
) {
    suspend operator fun invoke(): List<Url> = repository.getAllUrls()
}
