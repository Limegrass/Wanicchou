package data.room

import android.app.Application
import androidx.annotation.WorkerThread
import data.arch.info.definition.DefinitionFactory
import data.arch.info.vocabulary.related.RelatedVocabularyFactory
import data.arch.info.vocabulary.search.SearchWordVocabularyFactory
import data.arch.search.IDictionaryWebPage
import data.enums.MatchType
import data.room.database.WanicchouDatabase
import data.room.entity.*
import data.room.search.DatabaseSearchStrategyFactory
import data.web.DictionaryWebPageFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document

// TODO: Decision to search DB or online should occur here
// TODO: Make things nullable and do appropriate logic when null for everything
class VocabularyRepository(application: Application) {

    @WorkerThread
    fun getLatest() : List<Vocabulary> {
        return database.vocabularyDao().getLatest()
    }

    private val database = WanicchouDatabase.getInstance(application)

    // TODO: this should be late and not lazy
    // and it should run not blocking
    val dictionaries : List<Dictionary> by lazy {
        runBlocking(Dispatchers.IO){
            database.dictionaryDao().getAllDictionaries()
        }
    }


    fun removeVocabulary(vocabulary: Vocabulary) {
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

    fun getRelatedWords(vocabularyID: Long) : List<Vocabulary> {
        return database.vocabularyDao().getWordsRelatedToVocabularyID(vocabularyID)
    }

    @WorkerThread
    fun getDefinitions(vocabularyID : Long,
                       definitionLanguageCode: String,
                       dictionaryID: Long) : List<Definition> {
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

    fun addVocabularyTag(tagText: String,
               vocabularyID: Long){
        var tagID = database.tagDao().getExistingTagID(tagText)
        if(tagID == null){
            val tag = Tag(tagText)
            runBlocking (Dispatchers.IO){
                tagID = database.tagDao().insert(tag)
            }
        }
        val vocabularyTag = VocabularyTag(tagID!!, vocabularyID)
        GlobalScope.launch (Dispatchers.IO) {

            database.vocabularyTagDao().insert(vocabularyTag)
        }
    }

    fun getTags(vocabularyID: Long) : List<Tag> {
        return database.tagDao().getTagsForVocabularyID(vocabularyID)
    }
}
