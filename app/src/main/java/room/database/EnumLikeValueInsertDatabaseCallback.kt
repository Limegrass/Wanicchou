package room.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import room.dbo.entity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class EnumLikeValueInsertDatabaseCallback(private val context : Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        Executors.newSingleThreadExecutor().execute {
            val database = WanicchouDatabase(context)
            super.onCreate(db)
            runBlocking {
                insertLanguages(database)
            }
            runBlocking {
                insertDictionaries(database)
            }
            insertDefaultEntry(database)
        }
    }

    //<editor-fold desc="Sub methods">
    private suspend fun insertLanguages(database: WanicchouDatabase){
        for (language in data.enums.Language.values()){
            val entity = Language(language.name, language.languageCode, language.languageID)
            database.languageDao().insert(entity)
        }
    }

    private suspend fun insertDictionaries(database: WanicchouDatabase){
        for (dictionary in data.enums.Dictionary.values()){
            val entity = Dictionary(dictionary.dictionaryName,
                    dictionary.defaultVocabularyLanguage,
                    dictionary.defaultVocabularyLanguage,
                    dictionary.dictionaryID)
            database.dictionaryDao().insert(entity)
        }
    }

    private fun insertDefaultEntry(database: WanicchouDatabase){
        GlobalScope.launch {
            val defaultVocabulary = Vocabulary(
                    word = "和日帳",
                    pronunciation =  "わにっちょう",
                    pitch = "",
                    language = data.enums.Language.JAPANESE,
                    vocabularyID = 1)
            val defaultDefinition = Definition(
                    definitionText = "ある使えないアプリ。",
                    language = data.enums.Language.JAPANESE,
                    dictionary = data.enums.Dictionary.SANSEIDO,
                    vocabularyID = 1,
                    definitionID = 1)
            database.vocabularyDao().insert(defaultVocabulary)
            database.definitionDao().insert(defaultDefinition)
        }
    }
    //</editor-fold>
}