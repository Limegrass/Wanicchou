package data.room.context;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


@Dao
public interface ContextDao {

    /**
     * Inserts a linguistic context entry.
     * @param entity The entity consisting of a word and it's linguistic context.
     */
    @Insert
    public void insert(ContextEntity entity);

    /**
     * Updates a row in the database, if it exists.
     * @param entity The entity to updateNote.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(ContextEntity entity);

    /**
     * Deletes a row, if it exists.
     * @param entity The row to delete.
     */
    @Delete
    public void delete(ContextEntity entity);

    /**
     * Clears the database.
     */
    @Query("DELETE FROM WordContext")
    public void deleteAll();

    @Query("VACUUM")
    public void vacuum();
    /**


    /**
     * Gets the linguistic context for a particular word
     * @param word The word to search for.
     * @return The saved linguistic context of the word searched.
     */
    @Query("SELECT Context FROM WordContext WHERE Word = :word")
    public String getContextOf(String word);

    /**
     * Gets the linguistic context entity for a particular word
     * @param word The word to search for.
     * @return The saved linguistic context of the word searched.
     */
    @Query("SELECT * FROM WordContext WHERE Word = :word")
    public ContextEntity getContextEntityOf(String word);
}
