package data.room.repository

import data.arch.util.IRepository
import data.arch.util.ISearchProvider
import data.arch.models.INote
import data.arch.models.IVocabulary
import data.models.Note
import data.room.database.WanicchouDatabase
import data.room.dbo.entity.Vocabulary
import data.room.dbo.entity.VocabularyNote

class VocabularyNoteRepository(private val database : WanicchouDatabase)
    : IRepository<INote<IVocabulary>>,
    ISearchProvider<List<INote<IVocabulary>>, IVocabulary> {

    override suspend fun search(request: IVocabulary): List<INote<IVocabulary>> {
        val vocabularyID = Vocabulary.getVocabularyID(request, database) ?: return listOf()
        val vocabularyNotes = database.vocabularyNoteDao().getNotesForVocabulary(vocabularyID)
        val vocabulary = if(request is Vocabulary) {
            request
        }
        else {
            // Wrapping in entity class to speed up update and delete within this repo
            Vocabulary(request, vocabularyID)
        }
        return vocabularyNotes.map {
            Note(vocabulary as IVocabulary, it)
        }
    }

    override suspend fun insert(entity: INote<IVocabulary>) {
        val vocabularyID = Vocabulary.getVocabularyID(entity.topic, database)!!
        val vocabularyNote = VocabularyNote(entity.noteText, vocabularyID)
        database.vocabularyNoteDao().insert(vocabularyNote)
    }

    override suspend fun update(original: INote<IVocabulary>,
                                updated: INote<IVocabulary>) {
        if (original.topic != updated.topic) {
            throw IllegalArgumentException("Original and update notes reference different vocabulary words.")
        }
        val vocabularyID = Vocabulary.getVocabularyID(original.topic, database)!!
        database.vocabularyNoteDao().updateNote(updated.noteText, original.noteText, vocabularyID)
    }

    override suspend fun delete(entity: INote<IVocabulary>) {
        val vocabularyID = Vocabulary.getVocabularyID(entity.topic, database)!!
        database.vocabularyNoteDao().deleteNote(entity.noteText, vocabularyID)
    }

}