package data.room.notes;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface NoteDao {

    /**
     * Inserts a note.
     * @param note The note to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(NoteEntity note);

    /**
     * Updates a note, if it exists.
     * @param note The note entry to update
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(NoteEntity note);

    /**
     * Deletes a note, if it exists.
     * @param note The note to delete.
     */
    @Delete
    public void delete(NoteEntity note);

    /**
     * Clears the database.
     */
    @Query("DELETE FROM Notes")
    public void deleteAll();

    /**
     * Gets the note for a particular word
     * @param word The word to search for.
     * @return The note related to the word.
     */
    @Query("SELECT Note FROM Notes WHERE Word = :word")
    public String getNoteOf(String word);
}
