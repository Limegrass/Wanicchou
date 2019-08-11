package data.room.repository

import data.arch.models.ITaggedItem
import data.arch.models.IVocabulary
import data.arch.util.IRepository
import data.room.database.WanicchouDatabase
import data.room.dbo.composite.VocabularyAndTag
import data.room.dbo.entity.Tag
import data.room.dbo.entity.Vocabulary
import data.room.dbo.entity.VocabularyTag
import kotlinx.coroutines.runBlocking

class VocabularyTagRepository(private val database : WanicchouDatabase)
    : IRepository<ITaggedItem<IVocabulary>, IVocabulary> {

    override suspend fun search(request: IVocabulary): List<ITaggedItem<IVocabulary>> {
        val vocabularyID = Vocabulary.getVocabularyID(request, database) ?: return listOf()
        return database.vocabularyAndTagDao().getVocabularyAndTag(vocabularyID)
    }

    override suspend fun insert(entity: ITaggedItem<IVocabulary>) {
        val tagText = entity.tag
        val vocabulary = entity.item
        val vocabularyID = Vocabulary.getVocabularyID(vocabulary, database)!!
        val tagID = database.tagDao().getExistingTagID(tagText) ?: runBlocking {
            val tag = Tag(tagText.trim())
            database.tagDao().insert(tag)
        }
        val vocabularyTag = VocabularyTag(tagID, vocabularyID)
        database.vocabularyTagDao().insert(vocabularyTag)
    }

    /**
     * Updates the tag text for a given tag. Use Insert or Delete to add or remove relations
     */
    override suspend fun update(original: ITaggedItem<IVocabulary>,
                                updated: ITaggedItem<IVocabulary>) {
        val tagID = if (original is VocabularyAndTag) {
            original.tagEntity.tagID
        }
        else {
            database.tagDao().getExistingTagID(original.tag)!!
        }
        val tagEntity = Tag(updated.tag, tagID)
        database.tagDao().update(tagEntity)
    }

    override suspend fun delete(entity: ITaggedItem<IVocabulary>) {
        val vocabulary = entity.item
        val vocabularyID = Vocabulary.getVocabularyID(vocabulary, database)!!
        database.vocabularyTagDao().deleteVocabularyTag(vocabularyID, entity.tag)
    }
}