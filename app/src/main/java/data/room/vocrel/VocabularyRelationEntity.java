package data.room.vocrel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        tableName = "VocabularyRelation",
        indices = {@Index(value = {"VocabularyRelationID"}, unique = true)}
)
public class VocabularyRelationEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyRelationID")
    private int vocabularyRelationID;

    @ColumnInfo(name = "SearchVocabularyID")
    private int searchVocabularyID;

    @ColumnInfo(name = "ResultVocabularyID")
    private int resultVocabularyID;

    public VocabularyRelationEntity() {}

    public VocabularyRelationEntity(int vocabularyRelationID,
                                    int searchVocabularyID,
                                    int resultVocabularyID) {
        this.vocabularyRelationID = vocabularyRelationID;
        this.resultVocabularyID = resultVocabularyID;
        this.searchVocabularyID = searchVocabularyID;
    }


    public int getSearchVocabularyID() {
        return searchVocabularyID;
    }

    public void setSearchVocabularyID(int searchVocabularyID) {
        this.searchVocabularyID = searchVocabularyID;
    }

    public int getResultVocabularyID() {
        return resultVocabularyID;
    }

    public void setResultVocabularyID(int resultVocabularyID) {
        this.resultVocabularyID = resultVocabularyID;
    }

    public int getVocabularyRelationID() {
        return vocabularyRelationID;
    }

    public void setVocabularyRelationID(int vocabularyRelationID) {
        this.vocabularyRelationID = vocabularyRelationID;
    }
}
