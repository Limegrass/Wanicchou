//package data.room.dbo.composite
//
//import androidx.room.Embedded
//import androidx.room.Relation
//import data.arch.models.INote
//import data.arch.models.IVocabulary
//import data.room.dbo.entity.Vocabulary
//import data.room.dbo.entity.VocabularyNote
//
//data class VocabularyAndNote (
//        @Embedded
//        var vocabulary : Vocabulary,
//        @Embedded
//        var vocabularyNote : VocabularyNote)
//    : INote<IVocabulary> {
//    override val noteText: String
//        get() = vocabularyNote.noteText
//    override val topic: IVocabulary
//        get() = vocabulary
//}