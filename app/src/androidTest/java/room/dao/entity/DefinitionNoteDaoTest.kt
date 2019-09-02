package room.dao.entity

import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.AbstractDaoTest
import room.dbo.entity.Definition
import room.dbo.entity.DefinitionNote
import room.dbo.entity.Vocabulary
import kotlin.test.assertEquals

class DefinitionNoteDaoTest : AbstractDaoTest() {
    private fun insertDefinitionAndGetID() : Long {
        val vocabulary = Vocabulary("", "", "", data.enums.Language.JAPANESE)
        val vocabularyID = runBlocking {
            db.vocabularyDao().insert(vocabulary)
        }
        val definition = Definition("",
                data.enums.Language.JAPANESE,
                data.enums.Dictionary.SANSEIDO,
                vocabularyID)
        return runBlocking {
            db.definitionDao().insert(definition)
        }
    }

    @Test
    fun getNotesForDefinition_FindsNotes(){
        val definitionID = insertDefinitionAndGetID()
        val notes = listOf(
                DefinitionNote("1", definitionID),
                DefinitionNote("2", definitionID))
        runBlocking {
            for (note in notes){
                db.definitionNoteDao().insert(note)
            }
        }
        val definitionNotes = runBlocking {
            db.definitionNoteDao().getNotesForDefinition(definitionID)
        }

        assertEquals(notes.size, definitionNotes.size)
    }

    @Test
    fun updateNote_UpdatesDatabase() {
        val definitionID = insertDefinitionAndGetID()
        val notes = listOf(
                DefinitionNote("1", definitionID),
                DefinitionNote("2", definitionID))
        runBlocking {
            for (note in notes){
                db.definitionNoteDao().insert(note)
            }
        }
        runBlocking {
            db.definitionNoteDao().updateNote("3", "1", definitionID)
        }

        val definitionNotes = runBlocking {
            db.definitionNoteDao().getNotesForDefinition(definitionID)
        }
        assertEquals(notes.size, definitionNotes.size)
        assert(!definitionNotes.contains("1"))
        assert(definitionNotes.contains("2"))
        assert(definitionNotes.contains("3"))
    }

    @Test
    fun deleteNote_Success() {
        val definitionID = insertDefinitionAndGetID()
        val notes = listOf(
                DefinitionNote("1", definitionID),
                DefinitionNote("2", definitionID))
        runBlocking {
            for (note in notes) {
                db.definitionNoteDao().insert(note)
            }
        }
        runBlocking {
            db.definitionNoteDao().deleteNote("1", definitionID)
        }
        val definitionNotes = runBlocking {
            db.definitionNoteDao().getNotesForDefinition(definitionID)
        }
        assertEquals(notes.size - 1, definitionNotes.size)
        assert(!definitionNotes.contains("1"))
        assert(definitionNotes.contains("2"))
    }
}