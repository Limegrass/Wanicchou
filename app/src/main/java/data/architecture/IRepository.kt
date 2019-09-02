package data.architecture

interface IRepository<T, R> : ISearchProvider<List<T>, R> {
    suspend fun insert(entity: T)
    suspend fun update(original: T, updated: T)
    suspend fun delete(entity: T)
}