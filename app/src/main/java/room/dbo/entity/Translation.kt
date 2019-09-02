package room.dbo.entity


import androidx.room.*

@Entity(tableName = "Translation")
data class Translation (
        @ColumnInfo(name = "VocabularyLanguageID")
        var vocabularyLanguage: data.enums.Language,

        @ColumnInfo(name = "DefinitionLanguageID")
        var definitionLanguage: data.enums.Language,

        @ColumnInfo(name = "DictionaryID")
        var dictionary: data.enums.Dictionary,

        @ColumnInfo(name = "TranslationName")
        var translationName: String,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "TranslationID")
        var translationID: Long = 0){
        override fun toString(): String {
                return translationName
        }
}