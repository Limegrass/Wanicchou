package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Vocabulary")

data class Vocabulary (
    @ColumnInfo(name = "Word")
    val word: String = "",

    @ColumnInfo(name = "Pronunciation")
    val pronunciation: String = "",

    @ColumnInfo(name = "Pitch")
    val pitch: String = "",

    @ColumnInfo(name = "LanguageCode")
    val languageCode: String = "",

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyID")
    val vocabularyID: Int = 0
)
