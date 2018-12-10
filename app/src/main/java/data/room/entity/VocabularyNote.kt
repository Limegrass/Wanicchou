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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyNoteID")
    private val vocabularyNoteID: Int,

    @ColumnInfo(name = "NoteText")
    private var noteText: String = "",

    @ColumnInfo(name = "VocabularyID")
    private val vocabularyID: Int
)
