package room.repository

import data.architecture.IRepository
import data.models.INote
import data.models.IDefinition
import data.models.Note
import room.database.WanicchouDatabase
import room.dbo.entity.Definition
import room.dbo.entity.DefinitionNote

class DefinitionNoteRepository(private val database : WanicchouDatabase)
    : IRepository<INote<IDefinition>, IDefinition> {

    override suspend fun search(request: IDefinition): List<INote<IDefinition>> {
        val definitionID = Definition.getDefinitionID(database, request) ?: return listOf()
        val notes = database.definitionNoteDao().getNotesForDefinition(definitionID)
        return notes.map {
            Note(request, it)
        }
    }

    override suspend fun insert(entity: INote<IDefinition>) {
        val definitionID = Definition.getDefinitionID(database, entity.topic)!!
        val definitionNote = DefinitionNote(entity.noteText, definitionID)
        database.definitionNoteDao().insert(definitionNote)
    }

    override suspend fun update(original: INote<IDefinition>,
                                updated: INote<IDefinition>) {
        require(original.topic == updated.topic) {
            "Original and update notes reference different definitions."
        }
        val definitionID = Definition.getDefinitionID(database, original.topic)!!
        database.definitionNoteDao().updateNote(updated.noteText, original.noteText, definitionID)
    }

    override suspend fun delete(entity: INote<IDefinition>) {
        val definitionID = Definition.getDefinitionID(database, entity.topic)!!
        database.definitionNoteDao().deleteNote(entity.noteText, definitionID)
    }
}
