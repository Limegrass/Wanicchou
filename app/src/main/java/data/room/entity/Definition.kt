package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Definition",
        foreignKeys = [
            ForeignKey(
                    entity = Language::class,
                    parentColumns = ["LanguageID"],
                    childColumns = ["LanguageID"]),
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

    @ColumnInfo(name = "DefinitionText")
    var definitionText: String = "",

    @ColumnInfo(name = "LanguageID")
    var languageID: Int,

    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Int
)
