package data.room.dao

import androidx.room.*

interface BaseDao<T> {
    /**
     * Inserts the object into the database. Replaces on conflict
     * @param obj the object to insert into the database
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: T): Long

    /**
     * Updates the object in the database, if it exists. Replaces on conflict.
     * @param obj the updated object
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: T)

    /**
     * Deletes the object in the database, if it exists.
     * @param obj the object to find and delete from the database
     */
    @Delete
    suspend fun delete(obj: T)
}
