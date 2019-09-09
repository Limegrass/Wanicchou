package room.dbo.composite

import androidx.room.Embedded
import androidx.room.Relation
import data.models.IDictionaryEntry
import room.dbo.entity.Definition
import room.dbo.entity.Vocabulary

data class DictionaryEntry (
        @Embedded
        override var vocabulary : Vocabulary,
        @Relation(entity = Definition::class,
                  parentColumn = "VocabularyID",
                  entityColumn = "VocabularyID")
        override var definitions : List<Definition>) : IDictionaryEntry
