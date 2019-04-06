package data.room.entity

import androidx.room.*

@Entity(tableName = "VocabularyRelation",
        foreignKeys = [
            ForeignKey(entity = Vocabulary::class,
                       parentColumns = ["VocabularyID"],
                       childColumns = ["SearchVocabularyID"]),
            ForeignKey(entity = Vocabulary::class,
                       parentColumns = ["VocabularyID"],
                       childColumns = ["ResultVocabularyID"])
        ],

        indices = [Index(
                value = ["SearchVocabularyID",
                         "ResultVocabularyID"],
                unique = true
        )]
)
data class VocabularyRelation (
    @ColumnInfo(name = "SearchVocabularyID", index = true)
    var searchVocabularyID: Long,

    @ColumnInfo(name = "ResultVocabularyID", index = true)
    var resultVocabularyID: Long,

    @ColumnInfo(name = "MatchTypeBitmask")
    var matchTypeBitMask: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyRelationID")
    var vocabularyRelationID: Long = 0
)
