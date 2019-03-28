package data.room.entity

import androidx.room.Embedded
import androidx.room.Relation

class VocabularyInformation {
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
    var relatedWordIDs: List<Long> = listOf()

    @Relation(parentColumn = "VocabularyID",
              entityColumn = "VocabularyID",
              entity = VocabularyTag::class,
              projection = ["TagID"])
    var tags: List<Long> = listOf()
}