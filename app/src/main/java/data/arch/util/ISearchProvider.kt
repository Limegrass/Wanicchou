package data.arch.util

interface ISearchProvider<T, R> {
    suspend fun search(request : R) : T
}