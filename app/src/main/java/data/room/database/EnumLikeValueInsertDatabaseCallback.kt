package data.room.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import data.room.dbo.entity.*
import data.web.DictionarySearchProviderFactory
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
                insertMatchTypes(database)
            }
            runBlocking {
                insertDictionaries(database)
            }
            insertDictionaryMatchTypes(database)
            insertTranslations(database)
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

    private suspend fun insertMatchTypes(database: WanicchouDatabase){
        val dao = database.matchTypeDao()
        for (matchType in data.enums.MatchType.values()) {
            val entity = MatchType(matchType.name,
                    matchType.templateString,
                    matchType.matchTypeID)
            dao.insert(entity)
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

    private fun insertDictionaryMatchTypes(database: WanicchouDatabase){
        for (dictionary in data.enums.Dictionary.values()){
            GlobalScope.launch {
                val webPage = DictionarySearchProviderFactory(dictionary).get()
                val matchTypeIDs = webPage.supportedMatchTypes
                for (matchType in matchTypeIDs) {
                    val dictionaryMatchType = DictionaryMatchType(dictionary,
                                                                  matchType)
                    database.dictionaryMatchTypeDao().insert(dictionaryMatchType)
                }
            }
        }
    }

    // These last two are absolute trash
    private fun insertTranslations(database: WanicchouDatabase){
        GlobalScope.launch {
            val jjName = "国語"
            val jjTranslation = Translation(data.enums.Language.JAPANESE,
                    data.enums.Language.JAPANESE,
                    data.enums.Dictionary.SANSEIDO,
                    jjName)
            val japaneseEnglish = "和英"
            val jeTranslation = Translation(data.enums.Language.JAPANESE,
                    data.enums.Language.ENGLISH,
                    data.enums.Dictionary.SANSEIDO,
                    japaneseEnglish)
            val englishJapanese = "英和"
            val ejTranslation = Translation(data.enums.Language.ENGLISH,
                    data.enums.Language.JAPANESE,
                    data.enums.Dictionary.SANSEIDO,
                    englishJapanese)
            database.translationDao().insert(jjTranslation)
            database.translationDao().insert(jeTranslation)
            database.translationDao().insert(ejTranslation)
        }
    }

    private fun insertDefaultEntry(database: WanicchouDatabase){
        GlobalScope.launch {
            database.vocabularyDao().insert(Vocabulary.DEFAULT_VOCABULARY)
            database.definitionDao().insert(Definition.DEFAULT_DEFINITION)
        }
    }
    //</editor-fold>
}