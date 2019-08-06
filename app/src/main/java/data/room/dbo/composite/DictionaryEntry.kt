package data.room.dbo.composite

import androidx.room.Embedded
import androidx.room.Relation
import data.arch.models.IDictionaryEntry
import data.room.dbo.entity.Definition
import data.room.dbo.entity.Vocabulary

data class DictionaryEntry (
        @Embedded
        override var vocabulary : Vocabulary,
        @Relation(entity = Definition::class, parentColumn = "VocabularyID", entityColumn = "VocabularyID")
        override var definitions : List<Definition>) : IDictionaryEntry

//@DatabaseView("""
//SELECT v.VocabularyID   [VocabularyVocabularyID],
//       v.Word           [VocabularyWord],
//       v.Pitch          [VocabularyPitch],
//       v.Pronunciation  [VocabularyPronunciation],
//       v.LanguageID     [VocabularyLanguageID],
//       d.DefinitionID   [DefinitionDefinitionID],
//       d.LanguageID     [DefinitionLanguageID],
//       d.VocabularyID   [DefinitionVocabularyID],
//       d.DefinitionText [DefinitionDefinitionText],
//       d.DictionaryID   [DefinitionDictionaryID]
//FROM Vocabulary v
//JOIN Definition d
//    ON v.VocabularyID = d.VocabularyID""")
//
//data class DictionaryEntry (
//        @Embedded(prefix = "Vocabulary")
//        val vocabulary : Vocabulary,
//        @Embedded(prefix = "Definition")
//        val definition : Definition)
//         {
//     //TODO: Should I just create a domain object at point, or is it overkill
//     @Transient
//     var relatedVocabulary : List<Vocabulary>? = null
//     @Transient
//     var relationMatchType : data.enums.MatchType? = null
//
//}