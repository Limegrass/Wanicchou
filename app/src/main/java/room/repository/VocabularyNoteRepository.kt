package room.repository

import data.architecture.IRepository
import data.models.INote
import data.models.IVocabulary
import data.models.Note
import room.database.WanicchouDatabase
import room.dbo.entity.Vocabulary
import room.dbo.entity.VocabularyNote

class VocabularyNoteRepository(private val database : WanicchouDatabase)
    : IRepository<INote<IVocabulary>, IVocabulary> {

    override suspend fun search(request: IVocabulary): List<INote<IVocabulary>> {
        val vocabularyID = Vocabulary.getVocabularyID(database, request) ?: return listOf()
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
        val vocabularyID = Vocabulary.getVocabularyID(database, entity.topic)!!
        val vocabularyNote = VocabularyNote(entity.noteText, vocabularyID)
        database.vocabularyNoteDao().insert(vocabularyNote)
    }

    override suspend fun update(original: INote<IVocabulary>,
                                updated: INote<IVocabulary>) {
        require(original.topic == updated.topic) {
            "Original and update notes reference different vocabulary words."
        }
        val vocabularyID = Vocabulary.getVocabularyID(database, original.topic)!!
        database.vocabularyNoteDao().updateNote(updated.noteText, original.noteText, vocabularyID)
    }

    override suspend fun delete(entity: INote<IVocabulary>) {
        val vocabularyID = Vocabulary.getVocabularyID(database, entity.topic)!!
        database.vocabularyNoteDao().deleteNote(entity.noteText, vocabularyID)
    }

}