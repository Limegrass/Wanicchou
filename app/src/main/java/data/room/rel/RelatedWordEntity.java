package data.room.rel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import data.room.voc.VocabularyEntity;
import data.vocab.DictionaryType;

@Entity(
        tableName = "RelatedWords",
        indices = {@Index(value = {"RelatedWord", "DictionaryType"}, unique = true)}
)
public class RelatedWordEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "FKBaseWordId")
    @NonNull
    private int fkBaseWordId;

    @ColumnInfo(name = "RelatedWord")
    @NonNull
    private String relatedWord;

    @ColumnInfo(name = "DictionaryType")
    @NonNull
    private String dictionaryType;

    public RelatedWordEntity(){}

    public RelatedWordEntity(VocabularyEntity baseWord,
                             String relatedWord, String dictionaryType){
        fkBaseWordId = baseWord.getId();
        this.relatedWord = relatedWord;
        this.dictionaryType = dictionaryType;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }


    @NonNull
    public int getFkBaseWordId() {
        return fkBaseWordId;
    }

    public void setFkBaseWordId(@NonNull int fkBaseWordId) {
        this.fkBaseWordId = fkBaseWordId;
    }

    @NonNull
    public String getRelatedWord() {
        return relatedWord;
    }

    public void setRelatedWord(@NonNull String relatedWord) {
        this.relatedWord = relatedWord;
    }

    @NonNull
    public String getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(@NonNull String dictionaryType) {
        this.dictionaryType = dictionaryType;
    }
}
