package data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import data.room.entity.Tag

@Dao
interface TagDao : BaseDao<Tag> {
    @Query("""
        SELECT t.TagText
        FROM Tag t
        WHERE t.TagID = :tagID""")
    fun getTagByID(tagID : Int) : String

    //TODO: Test if it's actually returning null
    @Query("""
        SELECT t.*
        FROM Tag t
        WHERE t.TagText = :tag""")
    fun getTag(tag: String) : Tag

    @Query("""
        SELECT t.TagID
        FROM Tag t
        WHERE t.TagText = :tag""")
    fun getExistingTagID(tag: String) : Long?

    @Query("""
        SELECT t.*
        FROM Tag t
        JOIN VocabularyTag vt
            ON vt.TagID = t.TagID
        WHERE vt.VocabularyID = :vocabularyID""")
    fun getTagsForVocabularyID(vocabularyID: Long) : List<Tag>

    @Query("""
        SELECT t.*
        FROM Tag t
        WHERE t.TagID IN (:tagIDs)""")
    fun getTags(tagIDs : List<Int>) : LiveData<List<Tag>>
}
