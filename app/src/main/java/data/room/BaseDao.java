package data.room;

import android.arch.persistence.room.*;

public interface BaseDao<T> {
    /**
     * Inserts the object into the database.
     * @param obj the object to insert into the database
     */
    @Insert
    void insert(T obj);

    /**
     * Updates the object in the database, if it exists. Replaces on conflict.
     * @param obj the updated object
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(T obj);

    /**
     * Deletes the object in the database, if it exists.
     * @param obj the object to find and delete from the database
     */
    @Delete
    void delete(T obj);

}
