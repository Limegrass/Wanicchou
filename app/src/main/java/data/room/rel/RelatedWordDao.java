package data.room.rel;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import data.room.voc.VocabularyEntity;
import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;


/**
 * DAO of Room Persistence Library for the relation of words to each other.
 */
@Dao
public interface RelatedWordDao {
    /**
     * Insert the word relation into the database.
     * @param entity the word relation pairing entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(RelatedWordEntity entity);

    /**
     * Update the word relation in the database.
     * @param entity the word relation in the database to updateNote.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(RelatedWordEntity entity);

    /**
     * Delete the word relation from the database.
     * @param entity The word relation in the database to delete.
     */
    @Delete
    public void delete(RelatedWordEntity entity);

    /**
     * Query to clear the database
     */
    @Query("DELETE FROM VOCABULARYWORDS")
    public void deleteAll();

    /**
     * Query to request a list of entities relations for a particular word in the database.
     * @param fkBaseWordId The foreign key of the word
     * @return
     */
    @Query("SELECT * FROM RelatedWords WHERE FKBaseWordId = :fkBaseWordId")
    public List<RelatedWordEntity> getRelatedWordsFromId(VocabularyEntity fkBaseWordId);

    @Query("SELECT * FROM RelatedWords WHERE FKBaseWordId = :fkBaseWordId"
            + " AND DictionaryType = :dictionaryType")
    public List<RelatedWordEntity> getRelatedWordsFromId(VocabularyEntity fkBaseWordId,
                                                         DictionaryType dictionaryType);
}
