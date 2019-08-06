//package data.room.dbo.composite
//
//import androidx.room.Embedded
//import androidx.room.Relation
//import data.arch.models.INote
//import data.arch.models.IDefinition
//import data.room.dbo.entity.Definition
//import data.room.dbo.entity.DefinitionNote
//
//data class DefinitionAndNote (
//        @Embedded
//        var definition : Definition,
//        @Embedded
//        var definitionNote : DefinitionNote)
//    : INote<IDefinition> {
//    override val noteText: String
//        get() = definitionNote.noteText
//    override val topic: IDefinition
//        get() = definition
//}