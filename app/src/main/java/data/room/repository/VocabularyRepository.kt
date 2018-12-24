package data.room.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.webkit.WebView
import data.room.WanicchouDatabase
import data.room.dao.BaseDao
import data.room.entity.*
import data.vocab.model.DictionaryEntry
import data.vocab.model.DictionaryWebPage
import data.vocab.search.SearchProvider
import data.vocab.shared.MatchType
import data.vocab.shared.WordListEntry
import java.lang.ref.WeakReference

// TODO: Decision to search DB or online should occur here
// TODO: Make things nullable and do appropriate logic when null for everything
class VocabularyRepository(application: Application,
                           private val onQueryFinish : IVocabularyRepository.OnQueryFinish)
    : IVocabularyRepository {
    open class InsertEntryAsyncTask<T>(dao : BaseDao<T>)
        : AsyncTask<T, Void, Void>() {
        private val dao = WeakReference(dao)
        override fun doInBackground(vararg params: T?): Void? {
            dao.get()?.insert(params[0]!!)
            return null
        }
    }

    private class InsertDictionaryEntryAsyncTask(database : WanicchouDatabase,
                                                 onQueryFinish: IVocabularyRepository.OnQueryFinish,
                                                 val dictionaryEntry: DictionaryEntry,
                                                 val relatedWords: List<WordListEntry>) :
            AsyncTask<Void, Void, Void>() {
        val dbReference = WeakReference(database)
        val onQueryFinish = WeakReference(onQueryFinish)
        override fun doInBackground(vararg params: Void?): Void? {
            val database = dbReference.get()
            if (database != null){
                var vocabulary = Vocabulary(dictionaryEntry.word,
                        dictionaryEntry.pronunciation,
                        dictionaryEntry.pitch,
                        dictionaryEntry.wordLanguageCode)
                database.vocabularyDao().insert(vocabulary)
                val vocabularyList = database.vocabularyDao()
                        .getLatest()
                vocabulary = vocabularyList.value!![0]
                val dictionaryID = database.dictionaryDao()
                        .getDictionaryByName(dictionaryEntry.dictionary).value!!.dictionaryID
                val definition = Definition(dictionaryEntry.definition,
                                            dictionaryEntry.definitionLanguageCode,
                                            dictionaryID,
                                            vocabulary.vocabularyID)
                database.definitionDao().insert(definition)
                val definitionLists = mutableListOf<LiveData<List<Definition>>>()
                for (voc in vocabularyList.value!!){
                    val definitionList = database.definitionDao()
                            .getVocabularyDefinitions(voc.vocabularyID,
                                                      dictionaryEntry.definitionLanguageCode)
                    definitionLists.add(definitionList)
                }
                for (word in relatedWords){
                    //What's my plan here? Since I have a uniqueness constraint, I can insert
                    // Blanks and then if they happen to
                    // search again they'll itll reinsert and replace.
                    // But there's probably a better way that involves refactoring...

                }
                onQueryFinish.get()?.onQueryFinish(vocabularyList, definitionLists, relatedWords)
            }
            return null
        }
    }

    override fun saveResults(dictionaryEntry: DictionaryEntry,
                             relatedWords: List<WordListEntry>) {
        //Insert Vocabulary, Get Vocabulary ID, Get Dictionary ID, After both => Insert Definition
        InsertDictionaryEntryAsyncTask(database, onQueryFinish, dictionaryEntry, relatedWords)
    }

    val database = WanicchouDatabase.getInstance(application)

    override fun search(searchTerm: String,
                        wordLanguageCode: String,
                        definitionLanguageCode: String,
                        matchType: MatchType,
                        dictionary: String,
                        webView: WebView,
                        onPageParsed: DictionaryWebPage.OnPageParsed) {
        // Do whatever, then use the callback
        val vocabularyList = searchVocabularyDatabase(searchTerm,
                                                      matchType,
                                                      wordLanguageCode,
                                                      definitionLanguageCode)
        // search online
        if (vocabularyList.value!!.isEmpty()){
            val webPage = SearchProvider.getWebPage(dictionary)
            webPage.search(searchTerm,
                           wordLanguageCode,
                           definitionLanguageCode,
                           matchType,
                           webView,
                           onPageParsed)
        }
        else {
            val definitionList = getDefinitionList(vocabularyList, definitionLanguageCode)
            val firstMatch = vocabularyList.value!![0]
            val relatedWords = getRelatedWords(firstMatch.vocabularyID,
                                               definitionLanguageCode,
                                               dictionary)


            onQueryFinish.onQueryFinish(vocabularyList, definitionList, relatedWords)
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

    override fun getLatest(onQueryFinish: IVocabularyRepository.OnQueryFinish) {
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
        onQueryFinish.onQueryFinish(vocabularyList, definitionList, relatedWords)
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
