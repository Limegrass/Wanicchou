package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "DefinitionNote",
        foreignKeys = [
            ForeignKey(
                    entity = Definition::class,
                    parentColumns = ["DefinitionID"],
                    childColumns = ["DefinitionID"])
        ]
)
data class DefinitionNote (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionNoteID")
    var definitionNoteID: Int = 0,

    @ColumnInfo(name = "NoteText")
    var noteText: String = "",

    @ColumnInfo(name = "DefinitionID")
    var definitionID: Int
)
