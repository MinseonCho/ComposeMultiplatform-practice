package domain.usecase

import domain.model.UrlInfo
import domain.repository.UrlRepository

class GetSavedUrl(
    private val urlRepository: UrlRepository,
) {
    suspend operator fun invoke(id: Int): Response {
        return runCatching {
            urlRepository.getUrl(id = id)?.let {
                Response.Success(
                    urlInfo = it
                )
            } ?: Response.NotExist
        }.getOrElse {
            Response.Failure(throwable = it)
        }
    }

    sealed interface Response {
        data class Success(
            val urlInfo: UrlInfo,
        ) : Response

        data object NotExist : Response

        data class Failure(
            val throwable: Throwable,
        ) : Response
    }
}
