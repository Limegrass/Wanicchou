package room.dbo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    var vocabularyNoteID: Long = 0) {
    override fun toString(): String {
        return noteText
    }
}