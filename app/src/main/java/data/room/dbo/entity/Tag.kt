package data.room.dbo.entity
import androidx.room.*

@Entity(tableName = "Tag")
data class Tag (
    @ColumnInfo(name = "TagText")
    var tagText: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TagID")
    var tagID: Long = 0){
    override fun toString(): String {
        return tagText
    }
}