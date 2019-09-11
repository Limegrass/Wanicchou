package room.repository

import androidx.annotation.WorkerThread
import data.architecture.IRepository
import data.enums.MatchType
import data.models.IDictionaryEntry
import data.search.SearchRequest
import kotlinx.coroutines.runBlocking
import room.database.WanicchouDatabase
import room.dbo.entity.Definition
import room.dbo.entity.Vocabulary

class DictionaryEntryRepository(private val database : WanicchouDatabase)
    : IRepository<IDictionaryEntry, SearchRequest> {
    @WorkerThread
    override suspend fun search(request: SearchRequest) : List<IDictionaryEntry> {
        val formattedSearchTerm = request.matchType.templateString.format(request.searchTerm)
        return when(request.matchType){
            MatchType.WORD_EQUALS -> database.dictionaryEntryDao()
                    .searchWordEqual(formattedSearchTerm, request.vocabularyLanguage, request.definitionLanguage)
            MatchType.WORD_STARTS_WITH -> database.dictionaryEntryDao()
                    .searchWordLike(formattedSearchTerm, request.vocabularyLanguage, request.definitionLanguage)
            MatchType.WORD_ENDS_WITH -> database.dictionaryEntryDao()
                    .searchWordLike(formattedSearchTerm, request.vocabularyLanguage, request.definitionLanguage)
            MatchType.WORD_CONTAINS -> database.dictionaryEntryDao()
                    .searchWordLike(formattedSearchTerm, request.vocabularyLanguage, request.definitionLanguage)
            MatchType.WORD_WILDCARDS -> database.dictionaryEntryDao()
                    .searchWordLike(formattedSearchTerm, request.vocabularyLanguage, request.definitionLanguage)
            MatchType.DEFINITION_CONTAINS -> database.dictionaryEntryDao()
                    .searchDefinitionLike(formattedSearchTerm, request.vocabularyLanguage, request.definitionLanguage)
            MatchType.WORD_OR_DEFINITION_CONTAINS -> database.dictionaryEntryDao()
                    .searchWordOrDefinitionLike(formattedSearchTerm, request.vocabularyLanguage, request.definitionLanguage)
        }
    }

    override suspend fun insert(entity: IDictionaryEntry) {
        val vocabularyID = Vocabulary.getVocabularyID(database, entity.vocabulary) ?: runBlocking {
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

        val vocabularyID = Vocabulary.getVocabularyID(database, original.vocabulary)!!
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
        val vocabularyID = Vocabulary.getVocabularyID(database, entity.vocabulary)!!
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