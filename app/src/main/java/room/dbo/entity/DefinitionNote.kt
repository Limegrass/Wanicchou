package room.dbo.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "DefinitionNote",
        foreignKeys = [
            ForeignKey(
                    entity = Definition::class,
                    parentColumns = ["DefinitionID"],
                    childColumns = ["DefinitionID"],
                    onDelete = CASCADE)]
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