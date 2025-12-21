package ru.practicum.android.diploma.search.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//будет завершено после окончания настройки Network
val searchModule = module {
    // Context для NetworkConnectionChecker
    single { androidContext() }

//    single { NetworkConnectionChecker(get()) }

//    single { provideRetrofit(get()) }
//    single { provideVacancyApi(get()) }

//    single<NetworkClient> { NetworkClientImpl(get(), get()) }

//    single<VacancyRepository> { VacancyRepositoryImpl(get()) }

//    single<VacancyInteractor> { VacancyInteractorImpl(get()) }
}
