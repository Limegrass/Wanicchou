package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Tag")
data class Tag (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TagID")
    var tagID: Int = 0,

    @ColumnInfo(name = "TagText")
    var tagText: String
)

