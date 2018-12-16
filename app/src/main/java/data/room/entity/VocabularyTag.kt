package data.room.entity

import android.arch.persistence.room.*

@Entity(tableName = "VocabularyTag",
        foreignKeys = [
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["VocabularyID"])
        ]
)

data class VocabularyTag (
    @ColumnInfo(name = "TagID")
    val tagID: Int,

    @ColumnInfo(name = "VocabularyID")
    val vocabularyID: Int,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyTagID")
    val vocabularyTagID: Int = 0
)
