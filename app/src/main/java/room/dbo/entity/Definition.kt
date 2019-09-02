package room.dbo.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import data.models.IDefinition
import room.database.WanicchouDatabase
import kotlinx.coroutines.runBlocking

@Entity(tableName = "Definition",
        foreignKeys = [
            ForeignKey(
                    entity = Dictionary::class,
                    parentColumns = ["DictionaryID"],
                    childColumns = ["DictionaryID"],
                    onDelete = CASCADE),
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["VocabularyID"],
                    onDelete = CASCADE),
            ForeignKey(
                    entity = Language::class,
                    parentColumns = ["LanguageID"],
                    childColumns = ["LanguageID"],
                    onDelete = CASCADE)
        ],
        indices = [Index(
                value = arrayOf("DefinitionText",
                        "VocabularyID"),
                unique = true)]
)
data class Definition (
        @ColumnInfo(name = "DefinitionText")
        override var definitionText: String,

        @ColumnInfo(name = "LanguageID", index = true)
        override var language: data.enums.Language,

        @ColumnInfo(name = "DictionaryID", index = true)
        override var dictionary: data.enums.Dictionary,

        @ColumnInfo(name = "VocabularyID", index = true)
        var vocabularyID: Long,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "DefinitionID")
        var definitionID: Long = 0 )
    : IDefinition {
    constructor(definition : IDefinition, vocabularyID: Long, definitionID : Long = 0)
            : this(definition.definitionText,
                   definition.language,
                   definition.dictionary,
                   vocabularyID,
                   definitionID)

    companion object {
        /**
         * Gets the DefinitionID from the IDefinition if it's an instance of the entity class,
         * else will perform a database request for it from the database given
         */
        fun getDefinitionID(definition : IDefinition, database: WanicchouDatabase) : Long? {
            return if (definition is Definition){
                definition.definitionID
            }
            else runBlocking {
                database.definitionDao().getDefinitionID(definition.definitionText,
                        definition.dictionary,
                        definition.language)
            }
        }

        val DEFAULT_DEFINITION = Definition(
                definitionText = "ある使えないアプリ。",
                language = data.enums.Language.JAPANESE,
                dictionary = data.enums.Dictionary.SANSEIDO,
                vocabularyID = 1,
                definitionID = 1)
    }


    override fun toString(): String {
        return definitionText
    }
}