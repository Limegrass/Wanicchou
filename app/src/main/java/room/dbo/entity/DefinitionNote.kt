package room.dbo.entity

import androidx.room.*

@Entity(tableName = "DefinitionNote",
        foreignKeys = [
            ForeignKey(
                    entity = Definition::class,
                    parentColumns = ["DefinitionID"],
                    childColumns = ["DefinitionID"])
        ]
)
data class DefinitionNote (
    @ColumnInfo(name = "NoteText")
    var noteText: String = "",

    @ColumnInfo(name = "DefinitionID", index = true)
    var definitionID: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionNoteID")
    var definitionNoteID: Long = 0 ) {
    override fun toString(): String {
        return noteText
    }
}