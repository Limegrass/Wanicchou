package data.room.voc;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import data.room.BaseDao;


/**
 * DAO of Room Persistence Library for words and their definitions.
 */
@Dao
public interface VocabularyDao extends BaseDao<VocabularyEntity> {
}
