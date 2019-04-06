package data.room

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import data.arch.info.definition.DefinitionFactory
import data.arch.info.vocabulary.related.RelatedVocabularyFactory
import data.arch.info.vocabulary.search.SearchWordVocabularyFactory
import data.arch.search.IDictionaryWebPage
import data.arch.util.SingletonHolder
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
    // Is there even any benefit to using this considering how fast data retrieval is
    companion object : SingletonHolder<VocabularyRepository, Application>({
        VocabularyRepository(it)
    })

    fun updateDefinitionNote(note :DefinitionNote){
        GlobalScope.launch(Dispatchers.IO){
            database.definitionNoteDao().update(note)
        }
    }
    fun deleteDefinitionNote(note : DefinitionNote){
        GlobalScope.launch(Dispatchers.IO){
            database.definitionNoteDao().delete(note)
        }
    }
    suspend fun updateVocabularyNote(note :VocabularyNote){
        database.vocabularyNoteDao().update(note)
    }
    suspend fun deleteVocabularyNote(note : VocabularyNote){
        database.vocabularyNoteDao().delete(note)
    }

    fun getVocabulary(vocabularyID: Long): List<Vocabulary>{
        return database.vocabularyDao().getVocabulary(vocabularyID)
    }

    suspend fun updateTag(tag : Tag){
        database.tagDao().update(tag)
    }

    suspend fun deleteVocabularyTag(vocabularyID: Long, tagID : Long) {
        database.vocabularyTagDao().deleteVocabularyTag(vocabularyID, tagID)
    }

    val languages : List<data.room.entity.Language> by lazy {
        runBlocking (Dispatchers.IO){
            database.languageDao().getAllLanguages()
        }
    }

    val matchTypes : List<data.room.entity.MatchType> by lazy {
        runBlocking (Dispatchers.IO){
            database.matchTypeDao().getAllMatchTypes()
        }
    }

    suspend fun getDictionaryMatchTypes(dictionaryID : Long) : List<data.room.entity.MatchType>{
        return database.matchTypeDao().getDictionaryMatchTypes(dictionaryID)
    }

    @WorkerThread
    fun getLatest() : List<Vocabulary> {
        return database.vocabularyDao().getLatest()
    }

    fun getAllSavedVocabulary() : LiveData<List<Vocabulary>> {
        return database.vocabularyDao().getAll()
    }

    private val database = WanicchouDatabase.getInstance(application)

    // TODO: this should be late and not lazy
    // and it should run not blocking
    val dictionaries : List<Dictionary> by lazy {
        runBlocking(Dispatchers.IO){
            database.dictionaryDao().getAllDictionaries()
        }
    }

    suspend fun getDictionaryAvailableTranslations(dictionaryID: Long): List<Translation>{
        return database.translationDao().getDictionaryTranslations(dictionaryID)
    }


    fun removeVocabulary(vocabulary: Vocabulary) {
        GlobalScope.launch(Dispatchers.IO) {
            database.vocabularyDao().delete(vocabulary)
        }
    }


    private suspend fun insertVocabulary(document: Document,
                                         wordLanguageID: Long,
                                         webPage : IDictionaryWebPage) : Long {
        val vocabulary = SearchWordVocabularyFactory(document,
                                                     wordLanguageID,
                                                     webPage.dictionaryID).get()
        if (!vocabulary.word.isBlank()){
            return database.vocabularyDao().insert(vocabulary)
        }
        return -1L

    }

    private suspend fun insertDefinition(document: Document,
                                         definitionLanguageID: Long,
                                         vocabularyID : Long,
                                         webPage: IDictionaryWebPage) {
        val definition = DefinitionFactory(document,
                                           definitionLanguageID,
                                           webPage.dictionaryID,
                                           vocabularyID).get()
        database.definitionDao().insert(definition)
    }

    @WorkerThread
    suspend fun getRelatedVocabularyDefinition(relatedVocabulary: Vocabulary,
                                        definitionLanguageID: Long,
                                        dictionaryID: Long) : Definition {
        var definitions = database.definitionDao()
                                  .getVocabularyDefinitions(relatedVocabulary.vocabularyID,
                                                            definitionLanguageID,
                                                            dictionaryID)
        if (definitions == null){
            val webPage = DictionaryWebPageFactory(dictionaryID).get()
            val webPageDocument = webPage.search("${relatedVocabulary.word} ${relatedVocabulary.pronunciation}",
                                                relatedVocabulary.languageID,
                                                definitionLanguageID,
                                                MatchType.WORD_EQUALS)
            insertDefinition(webPageDocument,
                             definitionLanguageID,
                             relatedVocabulary.vocabularyID,
                             webPage)
            definitions = database.definitionDao()
                                  .getVocabularyDefinitions(relatedVocabulary.vocabularyID,
                                                            definitionLanguageID,
                                                            dictionaryID)
        }

        return definitions!!
    }

    @WorkerThread
    suspend fun databaseSearch(searchTerm: String,
                               wordLanguageID: Long,
                               definitionLanguageID: Long,
                               databaseMatchType : MatchType) : List<Vocabulary>{
        val split = searchTerm.trim().split(" ")
        return if(split.size == 2){
            val word = split[0]
            val pronunciation = split[1]
            val vocabularyID = database.vocabularyDao()
                    .getVocabularyID(word, pronunciation, wordLanguageID)
            database.vocabularyDao().getVocabulary(vocabularyID)
        }
        else{
            getVocabularyFromDatabase(searchTerm,
                    wordLanguageID,
                    definitionLanguageID,
                    databaseMatchType)
        }
    }

    suspend fun onlineSearch(searchTerm: String,
                             wordLanguageID: Long,
                             definitionLanguageID: Long,
                             matchType: MatchType,
                             dictionaryID: Long) : List<Vocabulary>{
        val webPage = DictionaryWebPageFactory(dictionaryID).get()
        val webPageDocument = webPage.search(searchTerm,
                                             wordLanguageID,
                                             definitionLanguageID,
                                             matchType)

        val vocabularyID = insertVocabulary(webPageDocument, wordLanguageID, webPage)

        if(vocabularyID != -1L) {
            insertDefinition(webPageDocument,
                    definitionLanguageID,
                    vocabularyID,
                    webPage)
            insertRelatedVocabulary(webPageDocument,
                                    wordLanguageID,
                                    vocabularyID,
                                    matchType,
                                    webPage)
        }

        return database.vocabularyDao().getVocabulary(vocabularyID)
    }

    @WorkerThread
    private suspend fun insertRelatedVocabulary(document : Document,
                                                wordLanguageID: Long,
                                                vocabularyID : Long,
                                                matchType : MatchType,
                                                webPage: IDictionaryWebPage){
        val relatedWords = RelatedVocabularyFactory(document, wordLanguageID, webPage.dictionaryID)
                .get()
                .distinct()
        for (relatedVocabulary in relatedWords) {
            var relatedVocabularyID = database.vocabularyDao().insert(relatedVocabulary)
            if (relatedVocabularyID == -1L){
                relatedVocabularyID = database.vocabularyDao()
                        .getVocabularyID(relatedVocabulary.word,
                                relatedVocabulary.pronunciation,
                                relatedVocabulary.languageID,
                                relatedVocabulary.pitch)
            }
            val vocabularyRelation = VocabularyRelation(vocabularyID,
                    relatedVocabularyID,
                    matchType.getBitmask())
            database.vocabularyRelationDao().insert(vocabularyRelation)
        }
    }

    fun getRelatedWords(vocabularyID: Long) : List<Vocabulary> {
        return database.vocabularyDao().getWordsRelatedToVocabularyID(vocabularyID)
    }

    @WorkerThread
    fun getDefinition(vocabularyID : Long,
                       definitionLanguageID: Long,
                       dictionaryID: Long) : Definition? {
        return database.definitionDao()
                       .getVocabularyDefinitions(vocabularyID,
                                                 definitionLanguageID,
                                                 dictionaryID)
    }

    @WorkerThread
    private suspend fun getVocabularyFromDatabase(searchTerm: String,
                                                  wordLanguageID: Long,
                                                  definitionLanguageID: Long,
                                                  matchType: MatchType) : List<Vocabulary> {
        val templateString = getTemplateString(matchType)
        val formattedSearchTerm = templateString.format(searchTerm)
        val searchStrategy = DatabaseSearchStrategyFactory(matchType).get()
        return searchStrategy.search(database,
                               formattedSearchTerm,
                               wordLanguageID,
                               definitionLanguageID)
    }

    private fun getTemplateString(matchType: MatchType) : String {
        val matchTypeID = matchType.getBitmask()
        return database.matchTypeDao()
                       .getTemplateString(matchTypeID)
    }

    suspend fun addVocabularyTag(tagText: String,
               vocabularyID: Long){
        var tagID = database.tagDao().getExistingTagID(tagText)
        if(tagID == null){
            val tag = Tag(tagText)
            runBlocking (Dispatchers.IO){
                tagID = database.tagDao().insert(tag)
            }
        }
        val vocabularyTag = VocabularyTag(tagID!!, vocabularyID)
        database.vocabularyTagDao().insert(vocabularyTag)
    }

    fun getTags(vocabularyID: Long) : List<Tag> {
        return database.tagDao().getTagsForVocabularyID(vocabularyID)
    }

    suspend fun addVocabularyNote(noteText : String, vocabularyID: Long){
        val note = VocabularyNote(noteText, vocabularyID)
        database.vocabularyNoteDao().insert(note)
    }

    fun getVocabularyNotes(vocabularyID: Long) : List<VocabularyNote>{
        return database.vocabularyNoteDao()
                       .getVocabularyNoteForVocabularyID(vocabularyID)

    }

    suspend fun addDefinitionNote(noteText : String, vocabularyID: Long){
        val note = DefinitionNote(noteText, vocabularyID)
        database.definitionNoteDao().insert(note)
    }

    fun getDefinitionNotes(definitionID: Long) : List<DefinitionNote>{
        return database.definitionNoteDao()
                       .getNotesForDefinitionID(definitionID)

    }

}
