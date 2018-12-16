package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "Tag")
data class Tag (
    @ColumnInfo(name = "TagText")
    val tagText: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TagID")
    val tagID: Int = 0
)

