package domain.usecase

import domain.repository.UrlRepository

class SaveUrlUseCase(
    private val repository: UrlRepository
) {
    suspend operator fun invoke(url: String) = repository.insertUrl(url)
}
