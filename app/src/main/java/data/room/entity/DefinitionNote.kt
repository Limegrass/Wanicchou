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
    @ColumnInfo(name = "NoteText")
    val noteText: String = "",

    @ColumnInfo(name = "DefinitionID")
    val definitionID: Int,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionNoteID")
    val definitionNoteID: Int = 0
)
