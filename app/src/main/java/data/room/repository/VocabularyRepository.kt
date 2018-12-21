package data.room.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.webkit.WebView
import data.core.OnDatabaseQuery
import data.core.OnJavaScriptCompleted
import data.room.WanicchouDatabase
import data.room.dao.*
import data.room.entity.*
import data.vocab.model.DictionaryEntry
import data.vocab.search.SearchProvider
import data.vocab.shared.MatchType
import data.vocab.shared.WordListEntry

//TODO: Decision to search DB or online should occur here
class VocabularyRepository(application: Application) : OnJavaScriptCompleted {
    private val vocabularyDao : VocabularyDao
    private val definitionDao : DefinitionDao
    private val dictionaryDao : DictionaryDao
    private val tagDao : TagDao
    private val vocabularyNoteDao : VocabularyNoteDao
    private val definitionNoteDao : DefinitionNoteDao
    private val vocabularyRelationDao : VocabularyRelationDao
    private val vocabularyTagDao : VocabularyTagDao

    init {
        val database: WanicchouDatabase = WanicchouDatabase.getInstance(application)
        vocabularyDao = database.vocabularyDao()
        definitionDao = database.definitionDao()
        dictionaryDao = database.dictionaryDao()
        tagDao = database.tagDao()
        vocabularyNoteDao = database.vocabularyNoteDao()
        definitionNoteDao = database.definitionNoteDao()
        vocabularyRelationDao = database.vocabularyRelationDao()
        vocabularyTagDao = database.vocabularyTagDao()
    }


    fun search(webView: WebView,
               databaseCallback: OnDatabaseQuery,
               dictionary: String,
               searchTerm: String = "",
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType = MatchType.WORD_EQUALS) {
        // Do whatever, then use the callback
        val vocabularyList = searchVocabularyDatabase(searchTerm,
                                                      matchType,
                                                      wordLanguageCode,
                                                      definitionLanguageCode)

        // search online
        if (vocabularyList.value!!.isEmpty()){
            val javascriptCallback = this
            val webPage = SearchProvider.getWebPage(dictionary)
            webPage.search(webView,
                           javascriptCallback,
                           databaseCallback,
                           searchTerm,
                           wordLanguageCode,
                           definitionLanguageCode,
                           matchType)
        }
        else {
            val definitionList = ArrayList<LiveData<List<Definition>>>()
            vocabularyList.value!!.forEach {
                val definitions = definitionDao.getVocabularyDefinitions(it.vocabularyID, definitionLanguageCode)
                definitionList.add(definitions)
            }

            val firstMatch = vocabularyList.value!![0]
            val relatedWords = getRelatedWords(firstMatch.vocabularyID,
                                               definitionLanguageCode,
                                               dictionary)


            databaseCallback.onQueryFinish(vocabularyList, definitionList, relatedWords)
        }
    }

    fun getLatest(): LiveData<List<Vocabulary>>{
        return vocabularyDao.getLatest()
    }

    fun getDefinitions(vocabularyID: Int,
                       definitionLanguageCode: String): List<LiveData<List<Definition>>>{

        return arrayListOf(definitionDao.getVocabularyDefinitions(vocabularyID, definitionLanguageCode))
    }

    fun getRelatedWords(vocabularyID: Int,
                        definitionLanguageCode: String,
                        dictionary: String): List<WordListEntry>{
        val webPage = SearchProvider.getWebPage(dictionary)
        val relatedVocabularyList = vocabularyDao.getWordsRelatedToVocabularyID(vocabularyID)

        return webPage.relatedWordFactory
                .getRelatedWords(relatedVocabularyList, definitionLanguageCode)
    }

    override fun onJavaScriptCompleted(dictionaryEntry: DictionaryEntry,
                                       relatedWords: List<WordListEntry>,
                                       definitionLanguageCode: String,
                                       onDatabaseQuery: OnDatabaseQuery) {
        val vocabularyEntity = Vocabulary(dictionaryEntry.word,
                dictionaryEntry.pronunciation,
                dictionaryEntry.pitch,
                dictionaryEntry.wordLanguageCode)
        vocabularyDao.insert(vocabularyEntity)

        val vocabularyList = searchVocabularyDatabase(dictionaryEntry.word,
                MatchType.WORD_EQUALS,
                dictionaryEntry.wordLanguageCode,
                dictionaryEntry.definitionLanguageCode)

        val dictionaryID = dictionaryDao
                .getDictionaryByName(dictionaryEntry.dictionary).dictionaryID

        val vocabularyID = vocabularyList.value!![0].vocabularyID
        val definitionEntity = Definition(dictionaryID,
                dictionaryEntry.definition,
                vocabularyID,
                dictionaryEntry.wordLanguageCode)
        definitionDao.insert(definitionEntity)

        val definitionList = ArrayList<LiveData<List<Definition>>>()
        definitionList.add(definitionDao.getVocabularyDefinitions(vocabularyID, definitionLanguageCode))
        onDatabaseQuery.onQueryFinish(vocabularyList, definitionList, relatedWords)
        //Add the entry to the database
        //Query the DB the added items
        // After, query the DB for the LiveData
        // I either need to maintain the list of related words somewhere
        // or generate it on going to an activity
        // Second option might be best, and don't even populate related words unless the user goes
    }

    private fun searchVocabularyDatabase(searchTerm: String,
                                         matchType: MatchType,
                                         wordLanguageCode: String,
                                         definitionLanguageCode: String): LiveData<List<Vocabulary>> {
        return when (matchType) {
            MatchType.WORD_EQUALS -> vocabularyDao.search(searchTerm, wordLanguageCode)
            MatchType.WORD_WILDCARDS -> vocabularyDao.searchWithWildcards(searchTerm, wordLanguageCode)
            MatchType.WORD_STARTS_WITH -> vocabularyDao.searchStartsWith(searchTerm, wordLanguageCode)
            MatchType.WORD_ENDS_WITH -> vocabularyDao.searchEndsWith(searchTerm, wordLanguageCode)
            MatchType.WORD_CONTAINS -> vocabularyDao.searchContains(searchTerm, wordLanguageCode)
            MatchType.DEFINITION_CONTAINS -> vocabularyDao
                    .searchDefinitionContains(searchTerm, definitionLanguageCode)
            MatchType.DEFINITION_OR_WORD_CONTAINS -> vocabularyDao
                    .searchWordOrDefinitionContains(searchTerm, wordLanguageCode, definitionLanguageCode)
        }
    }


    // I'm not too concerned about the cost of querying the DB at this
    // point since it's all local, but this could probably be improved
    private fun getTagID(tag: String) : Int {
        if (!tagDao.tagExists(tag)) {
            val tagEntity = Tag(tag)
            tagDao.insert(tagEntity)
        }
        return tagDao.getTag(tag).tagID
    }

    fun addTag(tag : String, word: String){
        val tagID = getTagID(tag)
        val vocabularyID = vocabularyDao.getVocabulary(word).vocabularyID
        val vocabularyTag = VocabularyTag(tagID, vocabularyID)
        vocabularyTagDao.insert(vocabularyTag)
    }

    fun deleteTag(tag: String){
        val tagEntity = tagDao.getTag(tag)
        tagDao.delete(tagEntity)
    }

    fun getVocabularyNotes(vocabularyID: Int): LiveData<List<VocabularyNote>> {
        return vocabularyNoteDao.getVocabularyNoteForVocabularyID(vocabularyID)
    }
}
