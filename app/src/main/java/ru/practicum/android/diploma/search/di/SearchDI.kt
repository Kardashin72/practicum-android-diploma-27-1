package ru.practicum.android.diploma.search.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.search.data.SearchRepositoryImpl
import ru.practicum.android.diploma.search.data.mapper.DtoMapper
import ru.practicum.android.diploma.search.data.mapper.FilterMapper
import ru.practicum.android.diploma.search.data.network.NetworkClient
import ru.practicum.android.diploma.search.data.network.NetworkClientImpl
import ru.practicum.android.diploma.search.data.network.SearchApi
import ru.practicum.android.diploma.search.data.network.provideOkHttpClient
import ru.practicum.android.diploma.search.data.network.provideRetrofit
import ru.practicum.android.diploma.search.data.storage.PrefsStorageClient
import ru.practicum.android.diploma.search.data.storage.StorageClient
import ru.practicum.android.diploma.search.data.storage.VacancyFilterStorageKeys
import ru.practicum.android.diploma.search.data.storage.VacancyFilterStorageRepositoryImpl
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.api.SearchRepository
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageInteractor
import ru.practicum.android.diploma.search.domain.api.VacancyFilterStorageRepository
import ru.practicum.android.diploma.search.domain.impl.SearchInteractorImpl
import ru.practicum.android.diploma.search.domain.impl.VacancyFilterStorageInteractorImpl
import ru.practicum.android.diploma.search.domain.model.VacancyFilter
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchFiltersViewModel
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchIndustryFilterViewModel
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchViewModel
import ru.practicum.android.diploma.search.utils.NetworkConnectionChecker

val searchDataModule = module {
    single { NetworkConnectionChecker(get()) }
    single { DtoMapper() }
    single { FilterMapper() }
    single { provideOkHttpClient { BuildConfig.API_ACCESS_TOKEN } }
    single { provideRetrofit(get()) }
    single { get<Retrofit>().create(SearchApi::class.java) }
    single<NetworkClient> { NetworkClientImpl(get(), get(), get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    single {
        androidContext().getSharedPreferences(
            VacancyFilterStorageKeys.VACANCY_FILTER_STORAGE_PREFS,
            Context.MODE_PRIVATE
        )
    }
    factory {
        Gson()
    }
    single<StorageClient<VacancyFilter>> {
        PrefsStorageClient(
            VacancyFilterStorageKeys.VACANCY_FILTER_STORAGE_KEY,
            object : TypeToken<VacancyFilter>() {}.type,
            get(),
            get()
        )
    }
    single<VacancyFilterStorageRepository> {
        VacancyFilterStorageRepositoryImpl(get())
    }
    single<VacancyFilterStorageInteractor> {
        VacancyFilterStorageInteractorImpl(get())
    }

}

val searchDomainModule = module {
    single<SearchInteractor> { SearchInteractorImpl(get(), get()) }
}

val searchPresentationModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SearchFiltersViewModel(get()) }
    viewModel { SearchIndustryFilterViewModel(get()) }
}

val searchModules = listOf(
    searchDataModule,
    searchDomainModule,
    searchPresentationModule
)
