package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Tag")
data class Tag (
    @ColumnInfo(name = "TagText")
    var tagText: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TagID")
    var tagID: Int = 0
)

