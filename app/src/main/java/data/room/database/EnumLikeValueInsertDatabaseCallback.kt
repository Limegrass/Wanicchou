package data.room.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import data.arch.lang.EnglishVocabulary
import data.arch.lang.JapaneseVocabulary
import data.room.entity.*
import data.web.DictionaryWebPageFactory
import data.web.sanseido.SanseidoWebPage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class EnumLikeValueInsertDatabaseCallback(private val context : Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        Executors.newSingleThreadExecutor().execute {
            val database = WanicchouDatabase.getInstance(context)
            super.onCreate(db)
            insertLanguages(database)
            runBlocking {
                insertDictionaries(database)
                insertMatchTypes(database)
            }
            insertDictionaryMatchTypes(database)
            insertTranslations(database)
            insertDefaultVocabulary(database)
        }
    }

    //<editor-fold desc="Sub methods">
    private fun insertLanguages(database: WanicchouDatabase){
        GlobalScope.launch {
            for (language in data.enums.Language.values()){
                val entity = Language(language.name, language.code, language.id)
                database.languageDao().insert(entity)
            }
        }
    }

    private suspend fun insertMatchTypes(database: WanicchouDatabase){
        val dao = database.matchTypeDao()
        for (matchType in data.enums.MatchType.values()) {
            val entity = MatchType(matchType.name,
                                      matchType.templateString,
                                      matchType.id)
            dao.insert(entity)
        }
    }

    private suspend fun insertDictionaries(database: WanicchouDatabase){
        val sanseido = Dictionary(SanseidoWebPage.DICTIONARY_NAME,
                                  JapaneseVocabulary.LANGUAGE_ID,
                                  JapaneseVocabulary.LANGUAGE_ID,
                                  SanseidoWebPage.DICTIONARY_ID)
        database.dictionaryDao().insert(sanseido)
    }
    private fun insertDictionaryMatchTypes(database: WanicchouDatabase){
        val sanseidoWebPage = DictionaryWebPageFactory(SanseidoWebPage.DICTIONARY_ID).get()
        val sanseidoMatchTypeIDs = sanseidoWebPage.getSupportedMatchTypes().map {
            it.id
        }
        GlobalScope.launch {
            for (matchTypeID in sanseidoMatchTypeIDs) {
                val dictionaryMatchType = DictionaryMatchType(SanseidoWebPage.DICTIONARY_ID,
                        matchTypeID)
                database.dictionaryMatchTypeDao()
                        .insert(dictionaryMatchType)
            }
        }
    }

    private fun insertTranslations(database: WanicchouDatabase){
        GlobalScope.launch {
            val jjName = "国語"
            val jjTranslation = Translation(JapaneseVocabulary.LANGUAGE_ID,
                    JapaneseVocabulary.LANGUAGE_ID,
                    SanseidoWebPage.DICTIONARY_ID,
                    jjName)
            val japaneseEnglish = "和英"
            val jeTranslation = Translation(JapaneseVocabulary.LANGUAGE_ID,
                    EnglishVocabulary.LANGUAGE_ID,
                    SanseidoWebPage.DICTIONARY_ID,
                    japaneseEnglish)
            val englishJapanese = "英和"
            val ejTranslation = Translation(EnglishVocabulary.LANGUAGE_ID,
                    JapaneseVocabulary.LANGUAGE_ID,
                    SanseidoWebPage.DICTIONARY_ID,
                    englishJapanese)
            database.translationDao().insert(jjTranslation)
            database.translationDao().insert(jeTranslation)
            database.translationDao().insert(ejTranslation)
        }
    }
    private fun insertDefaultVocabulary(database: WanicchouDatabase){
        GlobalScope.launch {
            val vocabulary = Vocabulary("和日帳","わにっちょう", "1",1, 1)
            val definition = Definition( "ある使えないアプリ。", 1, 1, 1)
            database.vocabularyDao().insert(vocabulary)
            database.definitionDao().insert(definition)
        }
    }
    //</editor-fold>
}
