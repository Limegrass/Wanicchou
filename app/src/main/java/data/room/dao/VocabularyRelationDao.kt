package data.room.dao

import androidx.room.Dao
import androidx.room.Query
import data.room.entity.Vocabulary
import data.room.entity.VocabularyRelation

@Dao
interface VocabularyRelationDao : BaseDao<VocabularyRelation>
