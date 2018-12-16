package data.room.dao

import android.arch.persistence.room.*

@Dao
interface BaseDao<T> {
    /**
     * Inserts the object into the database. Replaces on conflict
     * @param obj the object to insert into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    /**
     * Updates the object in the database, if it exists. Replaces on conflict.
     * @param obj the updated object
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

    /**
     * Deletes the object in the database, if it exists.
     * @param obj the object to find and delete from the database
     */
    @Delete
    fun delete(obj: T)

}
