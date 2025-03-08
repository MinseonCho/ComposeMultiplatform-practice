package domain.usecase

import domain.repository.UrlRepository

class DeleteSavedUrl(
    private val urlRepository: UrlRepository,
) {

    suspend operator fun invoke(id: Int): Response {
        return runCatching {
            urlRepository.deleteUrl(id = id)
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
