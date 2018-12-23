package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Vocabulary")

data class Vocabulary (
    @ColumnInfo(name = "Word")
    var word: String = "",

    @ColumnInfo(name = "Pronunciation")
    var pronunciation: String = "",

    @ColumnInfo(name = "Pitch")
    var pitch: String = "",

    @ColumnInfo(name = "LanguageCode")
    var languageCode: String = "",

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Int = 0 ) {
    override fun toString(): String {
        return word
    }
}
