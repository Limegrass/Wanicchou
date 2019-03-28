package data.room.entity

import androidx.room.*

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

    @ColumnInfo(name = "VocabularyID", index = true)
    var vocabularyID: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyNoteID")
    var vocabularyNoteID: Long ) {
    override fun toString(): String {
        return noteText
    }
}
