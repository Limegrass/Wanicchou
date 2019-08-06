package data.arch.util

//TODO: Consolidate this with ISearchProvider
interface IFilterableRepository<T, F> : IRepository<T>{
    suspend fun filter(filter : F) : List<T>
}
