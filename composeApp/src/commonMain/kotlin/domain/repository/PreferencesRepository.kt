package domain.repository

interface PreferencesRepository {
    fun getAdbPath(): String
    fun saveAdbPath(path: String)
} 