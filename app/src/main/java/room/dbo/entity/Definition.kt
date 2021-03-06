package room.dbo.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import data.models.IDefinition
import room.database.WanicchouDatabase

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
                value = arrayOf("VocabularyID", "LanguageID", "DictionaryID"),
                unique = true)]
)
data class Definition (
        @ColumnInfo(name = "DefinitionText")
        override var definitionText: String,

        @ColumnInfo(name = "LanguageID")
        override var language: data.enums.Language,

        @ColumnInfo(name = "DictionaryID")
        override var dictionary: data.enums.Dictionary,

        @ColumnInfo(name = "VocabularyID")
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
        suspend fun getDefinitionID(database: WanicchouDatabase,
                                    definition: IDefinition,
                                    vocabularyID: Long? = null) : Long? {
            return when {
                definition is Definition -> definition.definitionID
                vocabularyID != null -> database.definitionDao()
                        .getDefinitionIDByVocabularyID(vocabularyID,
                                definition.language,
                                definition.dictionary)
                else -> database.definitionDao()
                        .getDefinitionIDByDefinitionText(definition.definitionText,
                                definition.language,
                                definition.dictionary)
            }
        }

    }

    override fun toString(): String {
        return definitionText
    }
}