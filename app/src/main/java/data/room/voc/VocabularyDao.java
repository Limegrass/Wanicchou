package data.room.voc;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


/**
 * DAO of Room Persistence Library for words and their definitions.
 */
@Dao
public interface VocabularyDao {
    /**
     * Inserts the vocab entity into the database.
     * @param vocabulary the vocabulary entity to insert into the database.
     */
    @Insert
    void insertWord(VocabularyEntity vocabulary);

    /**
     * Updates the vocab entity in the database, if it exists. Replaces on conflict.
     * @param vocabulary the vocabulary entity to updateNote.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWord(VocabularyEntity vocabulary);

    /**
     * Deletes the vocab entity in the database, if it exists.
     * @param vocabulary the vocabulary entity to delete from the database.
     */
    @Delete
    void deleteWord(VocabularyEntity vocabulary);

    /**
     * Clears the database.
     */
    @Query("DELETE FROM VocabularyWords")
    void deleteAll();

    /**
     * Gets all words saved in the database.
     * @return A LiveData list of words saved in the database.
     */
    @Query("SELECT * FROM VocabularyWords")
    LiveData<List<VocabularyEntity>> getAllSavedWords();

    /**
     * Gets a certain number of words saved in the database,
     * ordered by searched order (recent first).
     * @param x The number of entries to return.
     * @return An array of words saved into the database, ordered by most recent to least.
     */
    @Query("SELECT * FROM VocabularyWords ORDER BY VocabularyId DESC LIMIT :x")
    VocabularyEntity[] getLastXSavedWords(int x);

    /**
     * Queries the database for a word and it's definition for a specific dictionary.
     * @param word The word to search for.
     * @param dictionaryType The dictionary type requested.
     * @return An entry of the word in the database, if it exists. Else, null.
     */
    @Query("SELECT * FROM VocabularyWords " +
            "WHERE word = :word AND DictionaryType = :dictionaryType LIMIT 1")
    VocabularyEntity getWord(String word, String dictionaryType);
}
