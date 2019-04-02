package data.room.entity


import androidx.room.*

@Entity(tableName = "Translation")
data class Translation (
        @ColumnInfo(name = "SourceLanguageID")
        var sourceLanguageID: Long,

        @ColumnInfo(name = "TargetLanguageID")
        var targetLanguageID: Long,

        @ColumnInfo(name = "DictionaryID")
        var dictionaryID: Long,

        @ColumnInfo(name = "TranslationName")
        var translationName: String,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "TranslationID")
        var translationID: Long = 0){
        override fun toString(): String {
                return translationName
        }
}
