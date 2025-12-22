package ru.practicum.android.diploma

import android.app.Application
import org.koin.core.context.GlobalContext.startKoin
import ru.practicum.android.diploma.favorites.vacancies.di.databaseModule
import ru.practicum.android.diploma.search.di.interactorModule
import ru.practicum.android.diploma.search.di.networkModule
import ru.practicum.android.diploma.search.di.repositoryModule
import ru.practicum.android.diploma.search.di.utilsModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                utilsModule,
                networkModule,
                repositoryModule,
                interactorModule,
                databaseModule
            )
        }
    }
}
