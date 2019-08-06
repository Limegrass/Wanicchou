package data.arch.util

interface IRepository<T> {
    suspend fun insert(entity : T)
    suspend fun update(original : T, updated : T)
    suspend fun delete(entity : T)
}