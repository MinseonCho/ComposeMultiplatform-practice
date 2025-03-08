package domain.usecase

import domain.repository.PreferencesRepository

class SaveAdbPath(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(path: String) {
        preferencesRepository.saveAdbPath(path)
    }
} 
