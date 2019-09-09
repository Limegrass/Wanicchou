package room.repository

import androidx.annotation.WorkerThread
import data.models.IDictionaryEntry
import data.search.SearchRequest
import data.architecture.IRepository
import room.database.WanicchouDatabase
import room.dbo.entity.Definition
import room.dbo.entity.Vocabulary
import room.search.DatabaseSearchStrategyFactory
import kotlinx.coroutines.runBlocking

class DictionaryEntryRepository(private val database : WanicchouDatabase)
    : IRepository<IDictionaryEntry, SearchRequest> {

    @WorkerThread
    override suspend fun search(request: SearchRequest) : List<IDictionaryEntry> {
        val searchStrategy = DatabaseSearchStrategyFactory(request.matchType).get()
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
        require(original.vocabulary == updated.vocabulary)
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
            val definitionID = Definition.getDefinitionID(database,
                    originalDefinitions[i],
                    vocabularyID)!!
            val definitionEntity = Definition(updatedDefinitions[i].definitionText,
                    updatedDefinitions[i].language,
                    updatedDefinitions[i].dictionary,
                    vocabularyID,
                    definitionID)
            database.definitionDao().update(definitionEntity)
        }
    }

    /**
     * Deletes the linked definition from the database. The vocabulary row remains intact.
     */
    override suspend fun delete(entity: IDictionaryEntry) {
        val vocabularyID = Vocabulary.getVocabularyID(entity.vocabulary, database)!!
        for (definition in entity.definitions) {
            val definitionID = Definition.getDefinitionID(database, definition, vocabularyID)!!
            val definitionEntity = Definition(definition.definitionText,
                    definition.language,
                    definition.dictionary,
                    vocabularyID,
                    definitionID)
            database.definitionDao().delete(definitionEntity)
        }
    }
}