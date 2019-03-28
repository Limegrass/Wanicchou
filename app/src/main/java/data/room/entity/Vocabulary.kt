package data.room.entity

import androidx.room.*
import java.util.*

@Entity(tableName = "Vocabulary",
        indices = [Index(
                value = arrayOf("Word",
                                "Pronunciation",
                                "LanguageCode",
                                "Pitch"),
                unique = true)]
)

data class Vocabulary (
    @ColumnInfo(name = "Word")
    var word: String = "",

    @ColumnInfo(name = "LanguageCode")
    var languageCode: String = "",

    @ColumnInfo(name = "Pronunciation")
    var pronunciation: String = "",

    @ColumnInfo(name = "Pitch")
    var pitch: String = "",

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Long = 0 ) {
    override fun toString(): String {
        return word
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Vocabulary){
            return false
        }
        return this.word == other.word
                && this.pronunciation == other.pronunciation
                && this.languageCode == other.languageCode
                && this.pitch == other.pitch
    }

    override fun hashCode(): Int {
        var hash = 17
        hash = hash * 31 + word.hashCode()
        hash = hash * 31 + pronunciation.hashCode()
        hash = hash * 31 + languageCode.hashCode()
        hash = hash * 31 + pitch.hashCode()
        return hash
    }
}
