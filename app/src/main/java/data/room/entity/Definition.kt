package data.room.entity

import androidx.room.*

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
    @ColumnInfo(name = "DefinitionText")
    var definitionText: String = "",

    @ColumnInfo(name = "LanguageCode")
    var languageCode: String = "",

    @ColumnInfo(name = "DictionaryID", index = true)
    var dictionaryID: Long,

    @ColumnInfo(name = "VocabularyID", index = true)
    var vocabularyID: Long,


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionID")
    var definitionID: Long = 0 ) {
    override fun toString(): String {
        return definitionText
    }
}
