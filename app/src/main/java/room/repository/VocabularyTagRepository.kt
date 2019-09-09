package room.repository

import data.models.ITaggedItem
import data.models.IVocabulary
import data.architecture.IRepository
import room.database.WanicchouDatabase
import room.dbo.composite.VocabularyAndTag
import room.dbo.entity.Tag
import room.dbo.entity.Vocabulary
import room.dbo.entity.VocabularyTag
import kotlinx.coroutines.runBlocking

class VocabularyTagRepository(private val database : WanicchouDatabase)
    : IRepository<ITaggedItem<IVocabulary>, IVocabulary> {

    override suspend fun search(request: IVocabulary): List<ITaggedItem<IVocabulary>> {
        val vocabularyID = Vocabulary.getVocabularyID(database, request) ?: return listOf()
        return database.vocabularyAndTagDao().getVocabularyAndTag(vocabularyID)
    }

    override suspend fun insert(entity: ITaggedItem<IVocabulary>) {
        val tagText = entity.tag
        val vocabulary = entity.item
        val vocabularyID = Vocabulary.getVocabularyID(database, vocabulary)!!
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
        require(original.item == updated.item) {
            "Original and updated tags reference different words."
        }
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
        val vocabularyID = Vocabulary.getVocabularyID(database, vocabulary)!!
        database.vocabularyTagDao().deleteVocabularyTag(entity.tag, vocabularyID)
    }
}