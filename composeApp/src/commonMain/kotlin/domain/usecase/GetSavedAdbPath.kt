package domain.usecase

import domain.repository.PreferencesRepository

class GetSavedAdbPath(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): String {
        return preferencesRepository.getAdbPath()
    }
} 
