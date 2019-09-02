package room.dao

import androidx.room.*

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: T): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(obj: T)
}
