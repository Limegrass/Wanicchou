package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Definition",
        foreignKeys = [
            ForeignKey(
                    entity = Dictionary::class,
                    parentColumns = ["DictionaryID"],
                    childColumns = ["DictionaryID"] ),
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["VocabularyID"] )
        ]
)
data class Definition (
    @ColumnInfo(name = "DictionaryID")
    val dictionaryID: Int,

    @ColumnInfo(name = "DefinitionText")
    val definitionText: String = "",

    @ColumnInfo(name = "VocabularyID")
    val vocabularyID: Int,

    @ColumnInfo(name = "LanguageCode")
    val languageCode: String = "",

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionID")
    val definitionID: Int = 0
)
