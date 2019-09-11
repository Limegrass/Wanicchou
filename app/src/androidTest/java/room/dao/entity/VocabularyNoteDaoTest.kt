package room.dao.entity

import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.AbstractDaoTest
import room.dbo.entity.VocabularyNote
import room.dbo.entity.Vocabulary
import kotlin.test.assertEquals

class VocabularyNoteDaoTest : AbstractDaoTest() {
    private fun insertVocabularyAndGetID() : Long {
        val vocabulary = Vocabulary("", "", "", data.enums.Language.JAPANESE)
        return runBlocking {
            db.vocabularyDao().insert(vocabulary)
        }
    }

    @Test
    fun getNotesForVocabulary_FindsNotes(){
        val vocabularyID = insertVocabularyAndGetID()
        val notes = listOf(
                VocabularyNote("1", vocabularyID),
                VocabularyNote("2", vocabularyID))
        runBlocking {
            for (note in notes){
                db.vocabularyNoteDao().insert(note)
            }
        }
        val vocabularyNotes = runBlocking {
            db.vocabularyNoteDao().getNotesForVocabulary(vocabularyID)
        }
        assertEquals(notes.size, vocabularyNotes.size)
    }

    @Test
    fun updateNote_UpdatesDatabase() {
        val vocabularyID = insertVocabularyAndGetID()
        val notes = listOf(
                VocabularyNote("1", vocabularyID),
                VocabularyNote("2", vocabularyID))
        runBlocking {
            for (note in notes){
                db.vocabularyNoteDao().insert(note)
            }
        }
        runBlocking {
            db.vocabularyNoteDao().updateNote("3", "1", vocabularyID)
        }
        val vocabularyNotes = runBlocking {
            db.vocabularyNoteDao().getNotesForVocabulary(vocabularyID)
        }
        assertEquals(notes.size, vocabularyNotes.size)
        assert(!vocabularyNotes.contains("1"))
        assert(vocabularyNotes.contains("2"))
        assert(vocabularyNotes.contains("3"))
    }

    @Test
    fun deleteNote_Success() {
        val vocabularyID = insertVocabularyAndGetID()
        val notes = listOf(
                VocabularyNote("1", vocabularyID),
                VocabularyNote("2", vocabularyID))
        runBlocking {
            for (note in notes){
                db.vocabularyNoteDao().insert(note)
            }
        }
        runBlocking {
            db.vocabularyNoteDao().deleteNote("1", vocabularyID)
        }
        val vocabularyNotes = runBlocking {
            db.vocabularyNoteDao().getNotesForVocabulary(vocabularyID)
        }
        assertEquals(notes.size - 1, vocabularyNotes.size)
        assert(!vocabularyNotes.contains("1"))
        assert(vocabularyNotes.contains("2"))
    }
}
