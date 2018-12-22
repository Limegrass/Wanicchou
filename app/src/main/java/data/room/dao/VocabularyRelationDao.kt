package data.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import data.room.entity.Vocabulary
import data.room.entity.VocabularyRelation

@Dao
interface VocabularyRelationDao : BaseDao<VocabularyRelation> {
}
