package room.dbo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Language")
data class Language (
        @ColumnInfo(name = "LanguageName")
        var languageName : String,

        @ColumnInfo(name = "LanguageCode")
        var languageCode : String,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "LanguageID")
        var languageID : Long = 0) {
    override fun toString(): String {
        return languageName
    }
}