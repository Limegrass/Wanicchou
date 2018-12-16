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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionID")
    var definitionID: Int,

    @ColumnInfo(name = "DictionaryID")
    var dictionaryID: Int,

    @ColumnInfo(name = "DefinitionText")
    var definitionText: String = "",

    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Int,

    @ColumnInfo(name = "LanguageCode")
    var languageCode: String = ""
)
