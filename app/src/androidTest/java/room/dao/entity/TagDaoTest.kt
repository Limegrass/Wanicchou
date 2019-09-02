package room.dao.entity

import kotlinx.coroutines.runBlocking
import org.junit.Test
import room.dao.AbstractDaoTest
import room.dbo.entity.Tag
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TagDaoTest : AbstractDaoTest() {
    @Test
    fun getExistingTagID_Exists_ReturnsID(){
        val tagText = "Test"
        runBlocking {
            db.tagDao().insert(Tag(tagText))
        }
        val tagID = db.tagDao().getExistingTagID(tagText)
        assertNotNull(tagID)
    }

    @Test
    fun getExistingTagID_TagTextMismatch_ReturnsNull(){
        val tagText = "Test"
        runBlocking {
            db.tagDao().insert(Tag(tagText))
        }
        val tagID = db.tagDao().getExistingTagID(tagText+"Whatever")
        assertNull(tagID)
    }
}