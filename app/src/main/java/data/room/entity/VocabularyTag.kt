package data.room.entity

import androidx.room.*

@Entity(tableName = "VocabularyTag",
        foreignKeys = [
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["VocabularyID"]),
            ForeignKey(
                    entity = Tag::class,
                    parentColumns = ["TagID"],
                    childColumns = ["TagID"])
        ],
        indices = [Index(
                value = ["TagID",
                         "VocabularyID"],
                unique = true
        )]
)

//Note: room requires a PK on an entity.
data class VocabularyTag (
    @ColumnInfo(name = "TagID")
    var tagID: Long,

    @ColumnInfo(name = "VocabularyID", index = true)
    var vocabularyID: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyTagID", index = true)
    var vocabularyTagID: Long = 0)
