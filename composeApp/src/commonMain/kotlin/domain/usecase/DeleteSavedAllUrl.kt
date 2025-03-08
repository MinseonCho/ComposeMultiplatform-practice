package domain.usecase

import domain.repository.UrlRepository

class DeleteSavedAllUrl(
    private val urlRepository: UrlRepository,
) {

    suspend operator fun invoke(): Response {
        return runCatching {
            urlRepository.deleteAllUrl()
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
