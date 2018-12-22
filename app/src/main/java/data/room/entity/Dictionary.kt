package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Dictionary")
data class Dictionary (
    @ColumnInfo(name = "DictionaryName")
    var dictionaryName: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DictionaryID")
    var dictionaryID: Int = 0
)
