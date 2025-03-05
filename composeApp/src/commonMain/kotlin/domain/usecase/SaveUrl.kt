package domain.usecase

import domain.repository.UrlRepository

class SaveUrl(
    private val repository: UrlRepository,
) {
    suspend operator fun invoke(
        url: String,
        memo: String?,
    ) = repository.insertUrl(
        url = url,
        memo = memo
    )
}
