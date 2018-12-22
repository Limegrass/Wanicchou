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
    var noteText: String = "",

    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Int,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyNoteID")
    var vocabularyNoteID: Int
)
