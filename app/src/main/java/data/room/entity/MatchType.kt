package data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MatchType")
data class MatchType (
        @ColumnInfo(name = "MatchTypeName")
        var matchTypeName : String,

        @ColumnInfo(name = "TemplateString")
        var templateString : String,

        @PrimaryKey
        @ColumnInfo(name = "MatchTypeID")
        var matchTypeID : Long
) {
    override fun toString(): String {
        return matchTypeName
    }
}
