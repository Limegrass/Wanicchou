package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Language")
data class Language (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LanguageID")
    var languageID: Int,

    @ColumnInfo(name = "CultureCode")
    var cultureCode: String = ""
)