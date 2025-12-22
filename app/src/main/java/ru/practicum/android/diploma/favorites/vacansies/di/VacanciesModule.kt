package ru.practicum.android.diploma.favorites.vacansies.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.vacansies.data.FavoritesDatabase
import ru.practicum.android.diploma.favorites.vacansies.data.db.dao.FavoriteVacancyDao

val databaseModule = module {
    single<FavoritesDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            FavoritesDatabase::class.java,
            "favorites_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single<FavoriteVacancyDao> { get<FavoritesDatabase>().favoriteVacancyDao() }
}

// пока комментирую
//val repositoryModule = module {
//    single { FavoriteVacancyRepository(get()) }
//}

// пока комментирую
//val viewModelModule = module {
//    viewModel { FavoritesVacanciesViewModel(get()) }
//}
