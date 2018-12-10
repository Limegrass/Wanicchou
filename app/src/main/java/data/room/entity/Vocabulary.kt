package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Vocabulary",
        foreignKeys = [
            ForeignKey(
                entity = Language::class,
                parentColumns = ["LanguageID"],
                childColumns = ["LanguageID"])
        ]
)

data class Vocabulary (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Int = 0,

    @ColumnInfo(name = "Word")
    var word: String = "",

    @ColumnInfo(name = "Pronunciation")
    var pronunciation: String = "",

    @ColumnInfo(name = "Pitch")
    var pitch: String = "",

    @ColumnInfo(name = "LanguageID")
    var languageID: Int = 0
)
