package data.room.dbo.composite

import androidx.room.DatabaseView
import androidx.room.Embedded
import data.arch.models.ITaggedItem
import data.arch.models.IVocabulary
import data.room.dbo.entity.Tag
import data.room.dbo.entity.Vocabulary

// Probably a view here would be sufficient : ITaggedItem

@DatabaseView("""
SELECT v.VocabularyID,
       v.Word,
       v.Pitch,
       v.Pronunciation,
       v.LanguageID,
       t.TagID,
       t.TagText
FROM Vocabulary v
JOIN VocabularyTag vt
    ON v.VocabularyID = vt.VocabularyID
JOIN Tag t
    ON vt.TagID = t.TagID """)
data class VocabularyAndTag(
        @Embedded
        val vocabulary : Vocabulary,
        @Embedded
        val tagEntity : Tag)
    : ITaggedItem<IVocabulary> {
    override val tag: String
        get() = tagEntity.tagText
    override val item: IVocabulary
        get() = vocabulary
}