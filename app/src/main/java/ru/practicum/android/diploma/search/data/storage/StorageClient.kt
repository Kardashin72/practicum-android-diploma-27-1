package ru.practicum.android.diploma.search.data.storage

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
    fun clearData()
}
