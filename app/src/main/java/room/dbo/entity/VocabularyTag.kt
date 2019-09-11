package room.dbo.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "VocabularyTag",
        foreignKeys = [
            ForeignKey(
                    entity = Vocabulary::class,
                    parentColumns = ["VocabularyID"],
                    childColumns = ["VocabularyID"],
                    onDelete = CASCADE),
            ForeignKey(
                    entity = Tag::class,
                    parentColumns = ["TagID"],
                    childColumns = ["TagID"],
                    onDelete = CASCADE)],
        indices = [Index(
                value = ["VocabularyID",
                         "TagID"],
                unique = true
        )]
)

//Note: room requires a PK on an entity.
data class VocabularyTag (
    @ColumnInfo(name = "TagID")
    var tagID: Long,

    @ColumnInfo(name = "VocabularyID")
    var vocabularyID: Long,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyTagID")
    var vocabularyTagID: Long = 0)