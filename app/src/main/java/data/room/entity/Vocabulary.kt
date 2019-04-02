package data.room.entity

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "Vocabulary",
        indices = [Index(
                value = arrayOf("Word",
                                "Pronunciation",
                                "LanguageID",
                                "Pitch"),
                unique = true)]
)

data class Vocabulary (
    @ColumnInfo(name = "Word")
    var word: String,

    @ColumnInfo(name = "LanguageID")
    var languageID: Long,

    @ColumnInfo(name = "Pronunciation")
    var pronunciation: String = "",

    @ColumnInfo(name = "Pitch")
    var pitch: String = "",

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Long = 0 ) : Parcelable {
    override fun toString(): String {
        return word
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Vocabulary){
            return false
        }
        return this.word == other.word
                && this.pronunciation == other.pronunciation
                && this.languageID == other.languageID
                && this.pitch == other.pitch
    }

    override fun hashCode(): Int {
        var hash = 17
        hash = hash * 31 + word.hashCode()
        hash = hash * 31 + pronunciation.hashCode()
        hash = hash * 31 + languageID.hashCode()
        hash = hash * 31 + pitch.hashCode()
        return hash
    }
}
