package domain.usecase

import domain.model.UrlInfo
import domain.repository.UrlRepository

class GetSavedUrlList(
    private val repository: UrlRepository,
) {
    suspend operator fun invoke(): Response {
        return runCatching {
            Response.Success(
                urls = repository.getAllUrls()
            )
        }.getOrElse {
            Response.Failure(throwable = it)
        }
    }

    sealed interface Response {
        data class Success(
            val urls: List<UrlInfo>,
        ) : Response

        data class Failure(
            val throwable: Throwable,
        ) : Response
    }
}
