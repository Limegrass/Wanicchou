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
class VocabularyRepository(application: Application) {

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


    private suspend fun insertVocabulary(document: Document,
                                         wordLanguageCode: String,
                                         webPage : IDictionaryWebPage) : Long {
        val vocabulary = SearchWordVocabularyFactory(document,
                                                     wordLanguageCode,
                                                     webPage.dictionaryID).get()

        return database.vocabularyDao().insert(vocabulary)
    }
    private suspend fun insertDefinition(document: Document,
                                         definitionLanguageCode: String,
                                         vocabularyID : Long,
                                         webPage: IDictionaryWebPage) {
        val definition = DefinitionFactory(document,
                                           definitionLanguageCode,
                                           webPage.dictionaryID,
                                           vocabularyID).get()
        database.definitionDao().insert(definition)
    }

    @WorkerThread
    suspend fun getRelatedVocabularyDefinition(relatedVocabulary: Vocabulary,
                                        definitionLanguageCode: String,
                                        dictionaryID: Long) : List<Definition>{
        var definitions = database.definitionDao()
                                  .getVocabularyDefinitions(relatedVocabulary.vocabularyID,
                                                            definitionLanguageCode,
                                                            dictionaryID)
        if (definitions.isEmpty()){
            val webPage = DictionaryWebPageFactory(dictionaryID).get()
            val webPageDocument = webPage.search("${relatedVocabulary.word} ${relatedVocabulary.pronunciation}",
                                                relatedVocabulary.languageCode,
                                                definitionLanguageCode,
                                                MatchType.WORD_EQUALS)
            insertDefinition(webPageDocument,
                             definitionLanguageCode,
                             relatedVocabulary.vocabularyID,
                             webPage)
            definitions = database.definitionDao()
                                  .getVocabularyDefinitions(relatedVocabulary.vocabularyID,
                                                            definitionLanguageCode,
                                                            dictionaryID)
        }
        return definitions
    }

    @WorkerThread
    suspend fun vocabularySearch(searchTerm: String,
                                 wordLanguageCode: String,
                                 definitionLanguageCode: String,
                                 matchType : MatchType,
                                 dictionaryID: Long) : List<Vocabulary>{
        val split = searchTerm.split(" ")
        val databaseResults : List<Vocabulary>
        databaseResults = if(split.size == 2){
            val word = split[0]
            val pronunciation = split[1]
            val vocabularyID = database.vocabularyDao()
                    .getVocabularyID(word, pronunciation, wordLanguageCode)
            database.vocabularyDao().getVocabulary(vocabularyID)
        }
        else{
            getVocabularyFromDatabase(searchTerm,
                    wordLanguageCode,
                    definitionLanguageCode,
                    matchType)

        }
        if (databaseResults.isNotEmpty()){
            return databaseResults
        }

        return getVocabularyFromOnline(searchTerm,
                wordLanguageCode,
                definitionLanguageCode,
                matchType,
                dictionaryID)
    }

    private suspend fun getVocabularyFromOnline(searchTerm: String,
                             wordLanguageCode: String,
                             definitionLanguageCode: String,
                             matchType: MatchType,
                             dictionaryID: Long) : List<Vocabulary>{
        val webPage = DictionaryWebPageFactory(dictionaryID).get()
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
    private suspend fun insertRelatedVocabulary(document : Document,
                                                wordLanguageCode: String,
                                                vocabularyID : Long,
                                                matchType : MatchType,
                                                webPage: IDictionaryWebPage){
        val relatedWords = RelatedVocabularyFactory(document, wordLanguageCode, webPage.dictionaryID)
                .get()
                .distinct()
        for (relatedVocabulary in relatedWords) {
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

    @WorkerThread
    override fun getDefinitions(vocabularyID : Long,
                      definitionLanguageCode: String,
                      dictionary: String) : List<Definition> {
        return database.definitionDao()
                       .getVocabularyDefinitions(vocabularyID,
                                                 definitionLanguageCode,
                                                 dictionaryID)
    }

    @WorkerThread
    private suspend fun getVocabularyFromDatabase(searchTerm: String,
                                                  wordLanguageCode: String,
                                                  definitionLanguageCode: String,
                                                  matchType: MatchType) : List<Vocabulary> {
        val templateString = getTemplateString(matchType)
        val formattedSearchTerm = templateString.format(searchTerm)
        val searchStrategy = DatabaseSearchStrategyFactory(matchType).get()
        return searchStrategy.search(database,
                               formattedSearchTerm,
                               wordLanguageCode,
                               definitionLanguageCode)
    }

    private fun getTemplateString(matchType: MatchType) : String {
        val matchTypeID = matchType.getMatchTypeID()
        return database.matchTypeDao().getTemplateString(matchTypeID)
    }

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

    fun getTags(vocabularyID: Long) : List<Tag> {
        return database.tagDao().getTagsForVocabularyID(vocabularyID)
    }
}
