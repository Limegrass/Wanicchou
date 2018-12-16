package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "VocabularyRelation",
        foreignKeys = [
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["SearchVocabularyID"]),
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["ResultVocabularyID"])
        ]
)
data class VocabularyRelation (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyRelationID")
    var vocabularyRelationID: Int,

    @ColumnInfo(name = "SearchVocabularyID")
    var searchVocabularyID: Int,

    @ColumnInfo(name = "ResultVocabularyID")
    var resultVocabularyID: Int
)
