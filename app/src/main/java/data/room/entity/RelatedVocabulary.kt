package data.room.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class RelatedVocabulary {
    @Embedded
    var vocabulary: Vocabulary? = null

    @Relation(parentColumn = "VocabularyID",
              entityColumn = "VocabularyID")
    var definitions: List<Definition> = listOf()

    @Relation(parentColumn = "VocabularyID",
            entityColumn = "VocabularyID")
    var vocabularyNotes: List<VocabularyNote> = listOf()

    @Relation(parentColumn = "VocabularyID",
            entityColumn = "SearchVocabularyID",
            entity = VocabularyRelation::class,
            projection = ["ResultVocabularyID"])
    var relatedWordIDs: List<Int> = listOf()

    @Relation(parentColumn = "VocabularyID",
            entityColumn = "VocabularyID",
            entity = VocabularyTag::class,
            projection = ["TagID"])
    var tags: List<Int> = listOf()
}
