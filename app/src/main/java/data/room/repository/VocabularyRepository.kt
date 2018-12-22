package data.room.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.webkit.WebView
import data.core.OnDatabaseQuery
import data.room.WanicchouDatabase
import data.room.entity.*
import data.vocab.model.DictionaryEntry
import data.vocab.search.SearchProvider
import data.vocab.shared.MatchType
import data.vocab.shared.WordListEntry

// TODO: Decision to search DB or online should occur here
// TODO: Make things nullable and do appropriate logic when null for everything
class VocabularyRepository(application: Application) : IVocabularyRepository {

    val database = WanicchouDatabase.getInstance(application)

    override fun search(webView: WebView,
                        databaseCallback: OnDatabaseQuery,
                        dictionary: String,
                        searchTerm: String,
                        wordLanguageCode: String,
                        definitionLanguageCode: String,
                        matchType: MatchType) {
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
            val definitionList = getDefinitionList(vocabularyList, definitionLanguageCode)

            val firstMatch = vocabularyList.value!![0]
            val relatedWords = getRelatedWords(firstMatch.vocabularyID,
                                               definitionLanguageCode,
                                               dictionary)


            databaseCallback.onQueryFinish(vocabularyList, definitionList, relatedWords)
        }
    }
    private fun getDefinitionList(vocabularyList: LiveData<List<Vocabulary>>,
                                  definitionLanguageCode: String):
            List<LiveData<List<Definition>>>{
        val definitionList = arrayListOf<LiveData<List<Definition>>>()
        vocabularyList.value!!.forEach {
            val definitions = database.definitionDao()
                    .getVocabularyDefinitions(it.vocabularyID, definitionLanguageCode)
            definitionList.add(definitions)
        }
        return definitionList
    }

    override fun getLatest(onDatabaseQuery: OnDatabaseQuery) {
        val vocabularyList = database.vocabularyDao().getLatest()
        val latestDefinition = database.definitionDao()
                .getLatestDefinition(vocabularyList.value!![0].vocabularyID)
        val definitionList = listOf(latestDefinition)
        val dictionaryID = latestDefinition.value!![0].dictionaryID
        val definitionLanguageCode = latestDefinition.value!![0].languageCode
        val dictionary = getDictionaryName(dictionaryID)
        val offlineList = vocabularyList.value!!

        val relatedWords = SearchProvider.getWebPage(dictionary)
                .relatedWordFactory.getRelatedWords(offlineList, definitionLanguageCode)
        onDatabaseQuery.onQueryFinish(vocabularyList, definitionList, relatedWords)
    }

    private fun getDictionaryName(dictionaryID: Int): String {
        return database.dictionaryDao().getDictionaryByID(dictionaryID).value!!.dictionaryName
    }

    fun getDefinitions(vocabularyID: Int,
                       definitionLanguageCode: String): List<LiveData<List<Definition>>>{

        return arrayListOf(database.definitionDao().getVocabularyDefinitions(vocabularyID, definitionLanguageCode))
    }

    fun getRelatedWords(vocabularyID: Int,
                        definitionLanguageCode: String,
                        dictionary: String): List<WordListEntry>{
        val webPage = SearchProvider.getWebPage(dictionary)
        val relatedVocabularyList = database.vocabularyDao().getWordsRelatedToVocabularyID(vocabularyID)

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
        database.vocabularyDao().insert(vocabularyEntity)

        val vocabularyList = searchVocabularyDatabase(dictionaryEntry.word,
                MatchType.WORD_EQUALS,
                dictionaryEntry.wordLanguageCode,
                dictionaryEntry.definitionLanguageCode)

        val dictionaryID = database.dictionaryDao()
                .getDictionaryByName(dictionaryEntry.dictionary).value!!.dictionaryID

        val vocabularyID = vocabularyList.value!![0].vocabularyID
        val definitionEntity = Definition(dictionaryID,
                dictionaryEntry.definition,
                vocabularyID,
                dictionaryEntry.wordLanguageCode)
        database.definitionDao().insert(definitionEntity)

        val definitionList = listOf(database.definitionDao().getVocabularyDefinitions(vocabularyID, definitionLanguageCode))
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
            MatchType.WORD_EQUALS -> database.vocabularyDao().search(searchTerm, wordLanguageCode)
            MatchType.WORD_WILDCARDS -> database.vocabularyDao().searchWithWildcards(searchTerm, wordLanguageCode)
            MatchType.WORD_STARTS_WITH -> database.vocabularyDao().searchStartsWith(searchTerm, wordLanguageCode)
            MatchType.WORD_ENDS_WITH -> database.vocabularyDao().searchEndsWith(searchTerm, wordLanguageCode)
            MatchType.WORD_CONTAINS -> database.vocabularyDao().searchContains(searchTerm, wordLanguageCode)
            MatchType.DEFINITION_CONTAINS -> database.vocabularyDao()
                    .searchDefinitionContains(searchTerm, definitionLanguageCode)
            MatchType.DEFINITION_OR_WORD_CONTAINS -> database.vocabularyDao()
                    .searchWordOrDefinitionContains(searchTerm, wordLanguageCode, definitionLanguageCode)
        }
    }


    // I'm not too concerned about the cost of querying the DB at this
    // point since it's all local, but this could probably be improved
    private fun getTagID(tag: String) : Int {
        if (!database.tagDao().tagExists(tag)) {
            val tagEntity = Tag(tag)
            database.tagDao().insert(tagEntity)
        }
        return database.tagDao().getTag(tag).tagID
    }

    fun addTag(tag : String, word: String){
        val tagID = getTagID(tag)
        val vocabularyID = database.vocabularyDao().getVocabulary(word).value!!.vocabularyID
        val vocabularyTag = VocabularyTag(tagID, vocabularyID)
        database.vocabularyTagDao().insert(vocabularyTag)
    }

    fun deleteTag(tag: String){
        val tagEntity = database.tagDao().getTag(tag)
        database.tagDao().delete(tagEntity)
    }

    fun getVocabularyNotes(vocabularyID: Int): LiveData<List<VocabularyNote>> {
        return database.vocabularyNoteDao().getVocabularyNoteForVocabularyID(vocabularyID)
    }
}
