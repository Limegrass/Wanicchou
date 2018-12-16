package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "VocabularyNote",
        foreignKeys = [
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["VocabularyID"])
        ]
)

data class VocabularyNote (
    @ColumnInfo(name = "NoteText")
    private val noteText: String = "",

    @ColumnInfo(name = "VocabularyID")
    private val vocabularyID: Int,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyNoteID")
    private val vocabularyNoteID: Int
)
