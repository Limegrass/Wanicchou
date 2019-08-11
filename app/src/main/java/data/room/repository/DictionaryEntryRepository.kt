package data.room.repository

import androidx.annotation.WorkerThread
import data.arch.models.IDictionaryEntry
import data.arch.search.IDictionarySource
import data.arch.search.SearchRequest
import data.arch.util.IRepository
import data.enums.Dictionary
import data.enums.Language
import data.enums.MatchType
import data.room.database.WanicchouDatabase
import data.room.dbo.entity.Definition
import data.room.dbo.entity.Vocabulary
import data.room.search.DatabaseSearchStrategyFactory
import data.web.DictionarySourceFactory
import kotlinx.coroutines.runBlocking

class DictionaryEntryRepository(private val database : WanicchouDatabase)
    : IRepository<IDictionaryEntry, SearchRequest>, IDictionarySource {

    override val supportedMatchTypes: Set<MatchType>
        get() = SUPPORTED_MATCH_TYPES
    override val supportedTranslations: Map<Language, Set<Language>>
        get() = SUPPORTED_TRANSLATIONS

    @WorkerThread
    override suspend fun search(request: SearchRequest) : List<IDictionaryEntry> {
        val searchStrategy = DatabaseSearchStrategyFactory(request.matchType).get()
        //TODO: Add RelatedVocabulary
        return searchStrategy.search(database, request)
    }

    override suspend fun insert(entity: IDictionaryEntry) {
        val vocabularyID = Vocabulary.getVocabularyID(entity.vocabulary, database) ?: runBlocking {
            val vocabularyEntity = Vocabulary(entity.vocabulary)
            database.vocabularyDao().insert(vocabularyEntity)
        }

        for (definition in entity.definitions){
            val definitionEntity = Definition(definition, vocabularyID)
            database.definitionDao().insert(definitionEntity)
        }
    }

    /**
     * Updates every vocabulary and definition entry provided.
     */
    override suspend fun update(original: IDictionaryEntry, updated: IDictionaryEntry) {
        val originalDefinitions = original.definitions
        val updatedDefinitions = updated.definitions
        require(originalDefinitions.size == updatedDefinitions.size)

        val vocabularyID = Vocabulary.getVocabularyID(original.vocabulary, database)!!
        val vocabularyEntity = Vocabulary(updated.vocabulary.word,
                updated.vocabulary.pronunciation,
                updated.vocabulary.pitch,
                updated.vocabulary.language,
                vocabularyID)
        database.vocabularyDao().update(vocabularyEntity)

        for (i in originalDefinitions.indices){
            val definitionID = Definition.getDefinitionID(originalDefinitions[i], database)!!
            val definitionEntity = Definition(updatedDefinitions[i].definitionText,
                    updatedDefinitions[i].language,
                    updatedDefinitions[i].dictionary,
                    definitionID)
            database.definitionDao().update(definitionEntity)
        }
    }

    /**
     * Deletes the linked definition from the database. The vocabulary row remains intact.
     */
    override suspend fun delete(entity: IDictionaryEntry) {
        for (definition in entity.definitions) {
            val definitionID = Definition.getDefinitionID(definition, database)!!
            val definitionEntity = Definition(definition.definitionText,
                    definition.language,
                    definition.dictionary,
                    definitionID)
            database.definitionDao().delete(definitionEntity)
        }
    }

    companion object {
        private val SUPPORTED_MATCH_TYPES = MatchType.values().toSet()
        private val SUPPORTED_TRANSLATIONS by lazy {
            val translations = mutableMapOf<Language, MutableSet<Language>>()
            for (dictionary in Dictionary.values()) {
                val dictionarySource = DictionarySourceFactory(dictionary).get()
                for (key in dictionarySource.supportedTranslations.keys) {
                    if (!translations.containsKey(key)){
                        translations[key] = mutableSetOf()
                    }
                    translations.getValue(key)
                            .addAll(dictionarySource.supportedTranslations.getValue(key))
                }
            }
            translations
        }
    }

}