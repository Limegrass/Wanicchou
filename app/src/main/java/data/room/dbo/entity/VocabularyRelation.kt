package data.room.dbo.entity

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
    var matchType: data.enums.MatchType,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyRelationID")
    var vocabularyRelationID: Long = 0)
// TODO: Remove this entirely?
//  It's just based on string matches vs their match type at the time and not any actual relations
//  And it makes everything worse (like having the fields in DictionaryEntry)


//TODO: The correct play is definitely to remove this entirely. It provides no useful information.
//  As long as I insert the vocabulary word itself into the DB, it'll pick it up on the like/equals/etc
//  It's isolated, Just need to write the deletion script