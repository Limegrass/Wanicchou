package data.room.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
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
        SELECT COUNT(0)
        FROM Tag t
        WHERE t.TagText = :tag""")
    fun tagExists(tag: String) : Boolean

    @Query("""
        SELECT t.*
        FROM Tag t
        JOIN VocabularyTag vt
            ON vt.TagID = t.TagID
        WHERE vt.VocabularyID = :vocabularyID""")
    fun getTagsForVocabularyID(vocabularyID: Int) : LiveData<List<Tag>>



    @Query(value = """
        SELECT t.*
        FROM Tag t
        WHERE t.TagID IN (:tagIDs)""")
    fun getTags(tagIDs : List<Int>) : LiveData<List<Tag>>
}
