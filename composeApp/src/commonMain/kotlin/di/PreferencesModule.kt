package di

import data.datasource.PreferencesDataSource
import data.repository.PreferencesRepositoryImpl
import domain.repository.PreferencesRepository
import domain.usecase.GetSavedAdbPath
import domain.usecase.SaveAdbPath
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val preferencesModule = module {
    // DataSource
    singleOf(::PreferencesDataSource)

    // Repository
    singleOf(::PreferencesRepositoryImpl).bind(PreferencesRepository::class)

    // UseCase
    factoryOf(::GetSavedAdbPath)
    factoryOf(::SaveAdbPath)
}
