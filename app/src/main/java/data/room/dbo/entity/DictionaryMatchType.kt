package data.room.dbo.entity

import androidx.room.*

@Entity(tableName = "DictionaryMatchType",
        foreignKeys = [
                ForeignKey(entity = Dictionary::class,
                        parentColumns = ["DictionaryID"],
                        childColumns = ["DictionaryID"]),
                ForeignKey(entity = MatchType::class,
                        parentColumns = ["MatchTypeID"],
                        childColumns = ["MatchTypeID"])
        ],
        indices = [Index(
                value = ["DictionaryID",
                        "MatchTypeID"],
                unique = true
        )]
)
data class DictionaryMatchType (
        @ColumnInfo(name = "DictionaryID", index = true)
        var dictionary: data.enums.Dictionary,

        @ColumnInfo(name = "MatchTypeID", index = true)
        var matchType: data.enums.MatchType,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "DictionaryMatchTypeID", index = true)
        var dictionaryMatchTypeID: Long = 0)