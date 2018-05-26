package data.room.rel;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import data.vocab.DictionaryType;


@Dao
public interface RelatedWordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(RelatedWordEntity entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(RelatedWordEntity entity);

    @Delete
    public void delete(RelatedWordEntity entity);

    @Query("DELETE FROM VOCABULARYWORDS")
    public void deleteAll();

    @Query("SELECT * FROM RelatedWords WHERE FKBaseWordId = :fkBaseWordId")
    public List<RelatedWordEntity> getRelatedWordsFromId(int fkBaseWordId);

    @Query("SELECT * FROM RelatedWords WHERE FKBaseWordId = :fkBaseWordId AND DictionaryType = :dictionaryType")
    public List<RelatedWordEntity> getRelatedWordsFromId(int fkBaseWordId, DictionaryType dictionaryType);
}
