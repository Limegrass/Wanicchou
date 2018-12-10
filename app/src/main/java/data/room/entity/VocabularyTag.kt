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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyTagID")
    var vocabularyTagID: Int,

    @ColumnInfo(name = "TagID")
    var tagID: Int,

    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Int
)

