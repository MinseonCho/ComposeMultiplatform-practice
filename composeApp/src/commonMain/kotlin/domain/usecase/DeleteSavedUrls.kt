package domain.usecase

import domain.repository.UrlRepository

class DeleteSavedUrls(
    private val urlRepository: UrlRepository,
) {

    suspend operator fun invoke(indices: List<Int>): Response {
        return runCatching {
            urlRepository.deleteUrls(indices = indices)
            Response.Success
        }.getOrElse {
            Response.Failure(throwable = it)
        }
    }

    sealed interface Response {
        data object Success : Response

        data class Failure(
            val throwable: Throwable,
        ) : Response
    }
}
