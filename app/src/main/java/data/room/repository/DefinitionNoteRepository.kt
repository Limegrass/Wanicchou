package data.room.repository

import data.arch.util.IRepository
import data.arch.models.INote
import data.arch.models.IDefinition
import data.models.Note
import data.room.database.WanicchouDatabase
import data.room.dbo.entity.Definition
import data.room.dbo.entity.DefinitionNote

class DefinitionNoteRepository(private val database : WanicchouDatabase)
    : IRepository<INote<IDefinition>, IDefinition>  {

    override suspend fun search(request: IDefinition): List<INote<IDefinition>> {
        val definitionID = Definition.getDefinitionID(request, database) ?: return listOf()
        val notes = database.definitionNoteDao().getNotesForDefinition(definitionID)
        return notes.map {
            Note(request, it)
        }
    }

    override suspend fun insert(entity: INote<IDefinition>) {
        val definitionID = Definition.getDefinitionID(entity.topic, database)!!
        val definitionNote = DefinitionNote(entity.noteText, definitionID)
        database.definitionNoteDao().insert(definitionNote)
    }

    override suspend fun update(original: INote<IDefinition>,
                                updated: INote<IDefinition>) {
        if (original.topic != updated.topic) {
            throw IllegalArgumentException("Original and update notes reference different definitions.")
        }
        val definitionID = Definition.getDefinitionID(original.topic, database)!!
        database.definitionNoteDao().updateNote(updated.noteText, original.noteText, definitionID)
    }

    override suspend fun delete(entity: INote<IDefinition>) {
        val definitionID = Definition.getDefinitionID(entity.topic, database)!!
        database.definitionNoteDao().deleteNote(entity.noteText, definitionID)
    }
}