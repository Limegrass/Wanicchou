package data.room.rel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import data.room.voc.VocabularyEntity;
import data.vocab.DictionaryType;

/**
 * An entry in the RelatedWords Database showcasing how words are related to each other.
 * One entry has a key in the main vocab database, which points towards words that a
 * SanseidoSearch said were related to the search.
 */
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

    /**
     * Empty constructor for the RPM to construct an entity in the DB.
     */
    public RelatedWordEntity(){}

    /**
     * Constructor given a vocab entity (needed for it's id), and the related words
     * and which dictionary it should search in.
     * @param baseWord An entity from the Vocabulary Database to link the word to.
     * @param relatedWord The word related to the main searched word.
     * @param dictionaryType The dictionary to search in.
     */
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
