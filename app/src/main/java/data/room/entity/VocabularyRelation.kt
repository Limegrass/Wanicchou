package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "VocabularyRelation",
        foreignKeys = [
            ForeignKey(entity = Vocabulary::class,
                       parentColumns = ["VocabularyID"],
                       childColumns = ["SearchVocabularyID"]),
            ForeignKey(entity = Vocabulary::class,
                       parentColumns = ["VocabularyID"],
                       childColumns = ["ResultVocabularyID"])
        ]
)
data class VocabularyRelation (
    @ColumnInfo(name = "SearchVocabularyID", index = true)
    var searchVocabularyID: Long,

    @ColumnInfo(name = "ResultVocabularyID", index = true)
    var resultVocabularyID: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyRelationID")
    var vocabularyRelationID: Long = 0 )
