package data.room.dao

import android.arch.persistence.room.Dao
import data.room.entity.VocabularyTag

@Dao
interface VocabularyTagDao : BaseDao<VocabularyTag>
