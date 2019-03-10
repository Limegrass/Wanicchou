package data.room

import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import data.arch.search.IDictionaryWebPage
import data.arch.vocab.IVocabularyRepository
import data.room.entity.*
import data.search.SearchProvider
import data.enums.MatchType
import data.arch.vocab.WordListEntry
import org.jsoup.nodes.Document

// TODO: Decision to search DB or online should occur here
// TODO: Make things nullable and do appropriate logic when null for everything
class VocabularyRepository(application: Application)
    : IVocabularyRepository {

    //TODO: Make sure that webviews are automatically recycled but I'm pretty sure

    val database = WanicchouDatabase.getInstance(application)
    private val dictionaries : List<Dictionary> by lazy {
        runBlocking{
            database.dictionaryDao().getAllDictionaries()
        }
    }

    override fun removeVocabulary(vocabulary: Vocabulary) {
        GlobalScope.launch(Dispatchers.IO) {
            database.vocabularyDao().delete(vocabulary)
        }
    }

//    private val onPageParsed = object : IDictionaryWebPage.OnPageParsed {
//        @WorkerThread
//        override suspend fun onPageParsed(document: Document,
//                                          wordLanguageCode: String,
//                                          definitionLanguageCode: String,
//                                          webPage : IDictionaryWebPage) {
//            //TODO: Remove NEVER on save, change it to either on app close or on new search
//            val vocabularyID = insertVocabulary(document, wordLanguageCode, webPage)
//
//            if(vocabularyID != -1L){
//                insertDefinition(document, definitionLanguageCode, vocabularyID, webPage)
//
//                val relatedWords = webPage.getRelatedWords(document, wordLanguageCode)
//                        .distinct()
//                for (word in relatedWords){
//                    GlobalScope.launch(Dispatchers.IO) {
//                        val alreadyInDatabase = database.vocabularyDao()
//                                .isAlreadyInserted(word.word,
//                                        word.pronunciation,
//                                        word.languageCode,
//                                        word.pitch)
//                        if(!alreadyInDatabase){
//                            val relatedVocabularyID = database.vocabularyDao().insert(word)
//                            val relatedVocabulary = VocabularyRelation(vocabularyID, relatedVocabularyID)
//                            database.vocabularyRelationDao().insert(relatedVocabulary)
//                        }
//                    }
//                }
//                val vocabularyList = getVocabulary(vocabularyID)
//                val relatedVocabularyList = database.vocabularyDao()
//                                                    .getWordsRelatedToVocabularyID(vocabularyID)
//                onQueryFinish.onQueryFinish(vocabularyList, relatedVocabularyList)
//            }
//        }
//    }

    private suspend fun insertVocabulary(document: Document,
                                         wordLanguageCode: String,
                                         webPage : IDictionaryWebPage) : Long {

        val vocabulary = webPage.getVocabulary(document, wordLanguageCode)
        return database.vocabularyDao().insert(vocabulary)
    }
    private suspend fun insertDefinition(document: Document,
                                         definitionLanguageCode: String,
                                         vocabularyID : Long,
                                         webPage: IDictionaryWebPage) {
        val definition = webPage.getDefinition(document, definitionLanguageCode)
        definition.vocabularyID = vocabularyID
        val dictionary = dictionaries.single{
            it.dictionaryName == webPage.dictionaryName
        }
        definition.dictionaryID = dictionary.dictionaryID
        database.definitionDao().insert(definition)
    }

    @WorkerThread
    override suspend fun vocabularySearch(searchTerm: String,
                                 wordLanguageCode: String,
                                 definitionLanguageCode: String,
                                 matchType : MatchType,
                                 dictionary: String) : List<Vocabulary>{
        val databaseResults = getVocabularyFromDatabase(searchTerm,
                                           wordLanguageCode,
                                           definitionLanguageCode,
                                           matchType)
        if (databaseResults.isNotEmpty()){
            return databaseResults
        }

        return getVocabularyFromOnline(searchTerm,
                wordLanguageCode,
                definitionLanguageCode,
                matchType,
                dictionary)
    }

    private suspend fun getVocabularyFromOnline(searchTerm: String,
                             wordLanguageCode: String,
                             definitionLanguageCode: String,
                             matchType: MatchType,
                             dictionary: String) : List<Vocabulary>{
        val webPage = SearchProvider.getWebPage(dictionary)
        val webPageDocument = webPage.search(searchTerm,
                                             wordLanguageCode,
                                             definitionLanguageCode,
                                             matchType)

        val vocabularyID = insertVocabulary(webPageDocument, wordLanguageCode, webPage)

        if(vocabularyID != -1L) {
            insertDefinition(webPageDocument,
                    definitionLanguageCode,
                    vocabularyID,
                    webPage)
            insertRelatedVocabulary(webPageDocument,
                                    wordLanguageCode,
                                    vocabularyID,
                                    matchType,
                                    webPage)

        }
        return database.vocabularyDao().getVocabulary(vocabularyID)
    }

    @WorkerThread
    private suspend fun insertRelatedVocabulary(webPageDocument : Document,
                                                wordLanguageCode: String,
                                                vocabularyID : Long,
                                                matchType : MatchType,
                                                webPage: IDictionaryWebPage){
        val relatedWords = webPage.getRelatedWords(webPageDocument, wordLanguageCode)
                .distinct()
        for (relatedVocabulary in relatedWords) {
            GlobalScope.launch(Dispatchers.IO) {
                var relatedVocabularyID = database.vocabularyDao().insert(relatedVocabulary)
                if (relatedVocabularyID == -1L){
                    relatedVocabularyID = database.vocabularyDao()
                            .getVocabularyID(relatedVocabulary.word,
                                    relatedVocabulary.pronunciation,
                                    relatedVocabulary.languageCode,
                                    relatedVocabulary.pitch)
                }
                val vocabularyRelation = VocabularyRelation(vocabularyID,
                        relatedVocabularyID,
                        matchType.getBitMask())
                database.vocabularyRelationDao().insert(vocabularyRelation)
            }
        }
    }

    @WorkerThread
    override fun getDefinitions(vocabularyID : Long,
                      definitionLanguageCode: String,
                      dictionary: String) : List<Definition> {
        return database.definitionDao()
                       .getVocabularyDefinitions(vocabularyID,
                                                 definitionLanguageCode,
                                                 dictionary)


    }



    @WorkerThread
    private suspend fun getVocabularyFromDatabase(searchTerm: String,
                                                  wordLanguageCode: String,
                                                  definitionLanguageCode: String,
                                                  matchType: MatchType) : List<Vocabulary> {
        val templateString = getTemplateString(matchType)
        val formattedSearchTerm = templateString.format(searchTerm)
        val factory = DatabaseSearchStrategyFactory(matchType)
        val strategy = factory.get()
        return strategy.search(database,
                               formattedSearchTerm,
                               wordLanguageCode,
                               definitionLanguageCode)
    }

    private fun getTemplateString(matchType: MatchType) : String {
        val matchTypeID = matchType.getMatchTypeID()
        return database.matchTypeDao().getTemplateString(matchTypeID)
    }


//    // TODO: Move construction of search method to activity
//    // Then pass it in here
//    // TODO: Create a search method factory somehow. Maybe context is sufficient?
//    override fun search(searchTerm: String,
//                        wordLanguageCode: String,
//                        definitionLanguageCode: String,
//                        matchType: MatchType,
//                        dictionary: String,
//                        lifecycleOwner: LifecycleOwner){
//        runBlocking (Dispatchers.IO){
//            //TODO: Make the return only the vocabulary IDs
//            // Then use that to generate the list of vocabularyInformation
//            val vocabularyList = searchVocabularyDatabase(searchTerm,
//                                                          matchType,
//                                                          wordLanguageCode,
//                                                          definitionLanguageCode)
//            if (vocabularyList.isEmpty()){
//
//                // move webpage getting to it's own factory or some shit
//                val webPage = SearchProvider.getWebPage(dictionary)
//                webPage.search(searchTerm,
//                        wordLanguageCode,
//                        definitionLanguageCode,
//                        matchType,
//                        onPageParsed)
//            }
//            else{
//                //TODO: Properly return every appropriate result.
//                val liveData = database.vocabularyDao()
//                                       .search(vocabularyList[0].vocabulary!!.vocabularyID)
//
//                onQueryFinish.onQueryFinish(liveData)
//            }
//
//        }
//
//    }

    private fun getDictionaryName(dictionaryID: Int): String {
        return database.dictionaryDao().getDictionaryByID(dictionaryID).value!!.dictionaryName
    }

    private fun getVocabulary(vocabularyID: Long): List<VocabularyInformation>{
        return database.vocabularyDao().search(vocabularyID)
    }

    private fun searchVocabularyDatabase(searchTerm: String,
                                         matchType: MatchType,
                                         wordLanguageCode: String,
                                         definitionLanguageCode: String): List<VocabularyInformation> {
        return when (matchType) {
            MatchType.WORD_EQUALS -> database.vocabularyDao().search(searchTerm, wordLanguageCode)
            MatchType.WORD_WILDCARDS -> database.vocabularyDao().searchWithWildcards(searchTerm, wordLanguageCode)
            MatchType.WORD_STARTS_WITH -> database.vocabularyDao().searchStartsWith(searchTerm, wordLanguageCode)
            MatchType.WORD_ENDS_WITH -> database.vocabularyDao().searchEndsWith(searchTerm, wordLanguageCode)
            MatchType.WORD_CONTAINS -> database.vocabularyDao().searchContains(searchTerm, wordLanguageCode)
            MatchType.DEFINITION_CONTAINS -> database.vocabularyDao()
                    .searchDefinitionContains(searchTerm, definitionLanguageCode)
            MatchType.WORD_OR_DEFINITION_CONTAINS -> database.vocabularyDao()
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
