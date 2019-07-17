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
            val japanese = Language(JapaneseVocabulary.LANGUAGE_NAME,
                    JapaneseVocabulary.LANGUAGE_CODE,
                    JapaneseVocabulary.LANGUAGE_ID)
            val english = Language(EnglishVocabulary.LANGUAGE_NAME,
                    EnglishVocabulary.LANGUAGE_CODE,
                    EnglishVocabulary.LANGUAGE_ID)
            database.languageDao().insert(japanese)
            database.languageDao().insert(english)
        }
    }
    private suspend fun insertMatchTypes(database: WanicchouDatabase){
        val wordEqualsEnum = data.enums.MatchType.WORD_EQUALS
        val wordEquals = MatchType(wordEqualsEnum.toString(), "%s", wordEqualsEnum.getBitmask())
        val wordStartsWithEnum = data.enums.MatchType.WORD_STARTS_WITH
        val wordStartsWith = MatchType(wordStartsWithEnum.toString(), "%s%%", wordStartsWithEnum.getBitmask())
        val wordEndsWithEnum = data.enums.MatchType.WORD_ENDS_WITH
        val wordEndsWith = MatchType(wordEndsWithEnum.toString(), "%%%s", wordEndsWithEnum.getBitmask())
        val wordContainsEnum = data.enums.MatchType.WORD_CONTAINS
        val wordContains = MatchType(wordContainsEnum.toString(), "%%%s%%", wordContainsEnum.getBitmask())
        val wordWildCardsEnum = data.enums.MatchType.WORD_WILDCARDS
        val wordWildCards = MatchType(wordWildCardsEnum.toString(), "%s", wordWildCardsEnum.getBitmask())
        val definitionContainsEnum =data.enums.MatchType.DEFINITION_CONTAINS
        val definitionContains = MatchType(definitionContainsEnum.toString(), "%%%s%%", definitionContainsEnum.getBitmask())
        val wordOrDefinitionContainsEnum = data.enums.MatchType.WORD_OR_DEFINITION_CONTAINS
        val wordOrDefinitionContains = MatchType(wordOrDefinitionContainsEnum.toString(), "%%%s%%", wordOrDefinitionContainsEnum.getBitmask())

        val dao = database.matchTypeDao()
        dao.insert(wordEquals)
        dao.insert(wordStartsWith)
        dao.insert(wordEndsWith)
        dao.insert(wordContains)
        dao.insert(wordWildCards)
        dao.insert(definitionContains)
        dao.insert(wordOrDefinitionContains)
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
            it.getBitmask()
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
