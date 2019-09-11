package room.dbo.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Dictionary")
data class Dictionary (
    @ColumnInfo(name = "DictionaryName")
    var dictionaryName: String,

    @ColumnInfo(name = "DefaultVocabularyLanguageID")
    var defaultVocabularyLanguage: data.enums.Language,

    @ColumnInfo(name = "DefaultDefinitionLanguageID")
    var defaultDefinitionLanguage: data.enums.Language,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DictionaryID")
    var dictionaryID: Long = 0
) : Parcelable {
    override fun toString(): String {
        return dictionaryName
    }
}