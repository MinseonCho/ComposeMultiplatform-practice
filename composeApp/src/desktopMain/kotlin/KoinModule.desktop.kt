import database.AppDatabase
import database.UrlDao
import database.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase>{
        getRoomDatabase()
    }
    single<UrlDao> { get<AppDatabase>().getUrlDao() }
}
