package data.repository

import data.datasource.PreferencesDataSource
import domain.repository.PreferencesRepository

class PreferencesRepositoryImpl(
    private val preferencesDataSource: PreferencesDataSource
) : PreferencesRepository {
    override fun getAdbPath(): String {
        return preferencesDataSource.getAdbPath()
    }

    override fun saveAdbPath(path: String) {
        preferencesDataSource.saveAdbPath(path)
    }
} 