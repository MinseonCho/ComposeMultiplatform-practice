import data.datasource.UrlLocalDataSource
import data.repository.UrlRepositoryImpl
import domain.repository.UrlRepository
import domain.usecase.GetSavedUrl
import domain.usecase.GetSavedUrlList
import domain.usecase.SaveUrl
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import ui.page.PageViewModel
import ui.savedurllist.SavedUrlViewModel

expect fun platformModule(): Module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            provideViewModelModule
                    + provideDataSourceModule
                    + provideRepositoryModule
                    + provideUseCaseModule
                    + platformModule()
        )
    }

val provideViewModelModule = module {
    viewModelOf(::PageViewModel)
    viewModelOf(::SavedUrlViewModel)
}

val provideDataSourceModule = module {
    singleOf(::UrlLocalDataSource)
}

val provideRepositoryModule = module {
    singleOf(::UrlRepositoryImpl).bind(UrlRepository::class)
}

val provideUseCaseModule = module {
    factoryOf(::GetSavedUrlList)
    factoryOf(::GetSavedUrl)
    factoryOf(::SaveUrl)
}
