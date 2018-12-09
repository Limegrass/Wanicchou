package data.room.def;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "Definition",
        indices = {@Index(value = {"DefinitionID"}, unique = true)}
)
public class DefinitionEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionID")
    private int definitionID;

    @ColumnInfo(name = "DefinitionText")
    @NonNull
    private String definitionText;

    @ColumnInfo(name = "LanguageID")
    private int languageID;

    @ColumnInfo(name = "VocabularyID")
    private int vocabularyID;

    public DefinitionEntity() {
        this.definitionText = "";
    }


    public DefinitionEntity(int definitionID,
                            @NonNull String definitionText,
                            int languageID,
                            int vocabularyID) {
        this.definitionID = definitionID;
        this.definitionText = definitionText;
        this.languageID = languageID;
        this.vocabularyID = vocabularyID;
    }


    public int getVocabularyID() {
        return vocabularyID;
    }

    public void setVocabularyID(int vocabularyID) {
        this.vocabularyID = vocabularyID;
    }

    public int getLanguageID() {
        return languageID;
    }

    public void setLanguageID(int languageID) {
        this.languageID = languageID;
    }

    @NonNull
    public String getDefinitionText() {
        return definitionText;
    }

    public int getDefinitionID() {
        return definitionID;
    }

    public void setDefinitionID(int definitionID) {
        this.definitionID = definitionID;
    }
}

