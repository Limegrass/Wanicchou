package data.room.entity

import androidx.room.*

@Entity(tableName = "VocabularyRelation",
        foreignKeys = [
            ForeignKey(entity = Vocabulary::class,
                       parentColumns = ["VocabularyID"],
                       childColumns = ["SearchVocabularyID"]),
            ForeignKey(entity = Vocabulary::class,
                       parentColumns = ["VocabularyID"],
                       childColumns = ["ResultVocabularyID"]),
            ForeignKey(entity = MatchType::class,
                       parentColumns = ["MatchTypeID"],
                       childColumns = ["MatchTypeID"])
        ],

        indices = [Index(
                value = ["SearchVocabularyID",
                         "ResultVocabularyID",
                         "MatchTypeID"],
                unique = true
        )]
)
data class VocabularyRelation (
    @ColumnInfo(name = "SearchVocabularyID", index = true)
    var searchVocabularyID: Long,

    @ColumnInfo(name = "ResultVocabularyID", index = true)
    var resultVocabularyID: Long,

    @ColumnInfo(name = "MatchTypeID", index = true)
    var matchTypeID: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyRelationID")
    var vocabularyRelationID: Long = 0
)
