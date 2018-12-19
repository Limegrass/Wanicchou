package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "AnkiNote",
        foreignKeys = [
            ForeignKey(
                    entity = Definition::class,
                    parentColumns = ["DictionaryID"],
                    childColumns = ["DictionaryID"])
        ]
)
data class AnkiNote (
        @ColumnInfo(name = "DictionaryID")
        val definitionID: Int,

        @ColumnInfo(name = "AnkiNoteID")
        val ankiNoteID: Long
)