package data.room.dao

import android.arch.persistence.room.Query
import data.room.entity.Vocabulary
import data.room.entity.VocabularyRelation

interface VocabularyRelationDao : BaseDao<VocabularyRelation> {
    @Query("""
        SELECT v.*
        FROM Vocabulary v
        JOIN VocabularyRelation vr
            ON vr.SearchVocabularyID = v.VocabularyID
        WHERE vr.SearchVocabularyID = :vocabularyID """)
    fun getWordsRelatedToVocabularyID(vocabularyID: Int): List<Vocabulary>
}
