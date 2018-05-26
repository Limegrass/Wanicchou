package data.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface VocabularyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertWord(VocabularyEntity vocabulary);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateWord(VocabularyEntity vocabulary);

    @Delete
    public void deleteWord(VocabularyEntity vocabulary);

    @Query("DELETE FROM VocabularyWords")
    public void deleteAll();

    @Query("SELECT * FROM VocabularyWords")
    public LiveData<List<VocabularyEntity>> getAllSavedWords();

    @Query("SELECT * FROM VocabularyWords ORDER BY id DESC LIMIT :x")
    public VocabularyEntity[] getLastXSavedWords(int x);

    @Query("SELECT * FROM VocabularyWords " +
            "WHERE word = :word AND dictionaryType = :dictionaryType LIMIT 1")
    public VocabularyEntity getWord(String word, String dictionaryType);
}
