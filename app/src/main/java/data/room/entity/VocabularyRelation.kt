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
    @ColumnInfo(name = "SearchVocabularyID")
    val searchVocabularyID: Int,

    @ColumnInfo(name = "ResultVocabularyID")
    val resultVocabularyID: Int,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyRelationID")
    val vocabularyRelationID: Int = 0
)
