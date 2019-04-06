package data.room.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Dictionary")
data class Dictionary (
    @ColumnInfo(name = "DictionaryName")
    var dictionaryName: String,

    @ColumnInfo(name = "DefaultVocabularyLanguageID")
    var defaultVocabularyLanguageID: Long,

    @ColumnInfo(name = "DefaultDefinitionLanguageID")
    var defaultDefinitionLanguageID: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DictionaryID")
    var dictionaryID: Long = 0
) : Parcelable {
    override fun toString(): String {
        return dictionaryName
    }
}
