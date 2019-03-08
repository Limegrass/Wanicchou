package data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MatchType")
data class MatchType (
        @ColumnInfo(name = "MatchTypeName")
        var matchTypeName : String,

        @ColumnInfo(name = "BitMask")
        var bitMask : Int = 0,

        @ColumnInfo(name = "TemplateString")
        var templateString : String,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "MatchTypeID")
        var matchTypeID : Long = 0
) {
    override fun toString(): String {
        return matchTypeName
    }
}
