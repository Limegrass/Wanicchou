package data.architecture

interface ISearchProvider<T, R> {
    suspend fun search(request : R) : T
}