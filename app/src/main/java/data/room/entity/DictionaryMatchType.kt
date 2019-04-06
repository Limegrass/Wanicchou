package data.room.entity

import androidx.room.*

@Entity(tableName = "DictionaryMatchType",
        foreignKeys = [
                ForeignKey(entity = Dictionary::class,
                        parentColumns = ["DictionaryID"],
                        childColumns = ["DictionaryID"]),
                ForeignKey(entity = MatchType::class,
                        parentColumns = ["MatchTypeBitmask"],
                        childColumns = ["MatchTypeBitmask"])
        ],
        indices = [Index(
                value = ["DictionaryID",
                    "MatchTypeBitmask"],
                unique = true
        )]
)
data class DictionaryMatchType (
        @ColumnInfo(name = "DictionaryID", index = true)
        var dictionaryID: Long,

        @ColumnInfo(name = "MatchTypeBitmask", index = true)
        var matchTypeBitmask: Long,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "DictionaryMatchTypeID", index = true)
        var dictionaryMatchTypeID: Long = 0)