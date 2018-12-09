package data.room.voctag;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        tableName = "VocabularyTag",
        indices = {@Index(value = {"VocabularyTagID"}, unique = true)}
)
public class VocabularyTagEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyTagID")
    private int vocabularyTagID;

    @ColumnInfo(name = "TagID")
    private int tagID;

    @ColumnInfo(name = "VocabularyID")
    private int vocabularyID ;

    public VocabularyTagEntity(){}

    public VocabularyTagEntity(int tagID, int vocabularyID) {
        this.tagID = tagID;
        this.vocabularyID = vocabularyID;
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public int getVocabularyID() {
        return vocabularyID;
    }

    public void setVocabularyID(int vocabularyID) {
        this.vocabularyID = vocabularyID;
    }

    public int getVocabularyTagID() {
        return vocabularyTagID;
    }

    public void setVocabularyTagID(int vocabularyTagID) {
        this.vocabularyTagID = vocabularyTagID;
    }
}

