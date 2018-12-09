package data.room.voc;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Vocabulary Entry for the Room Persistence Library, for Words and their definitions.
 */
@Entity(
        tableName = "Vocabulary",
        indices = {@Index(value = {"VocabularyID"}, unique = true)}
)
public class VocabularyEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyID")
    private int vocabularyID;

    @ColumnInfo(name = "Word")
    @NonNull
    private String word;

    @ColumnInfo(name = "Pronunciation")
    @NonNull
    private String pronunciation;

    @ColumnInfo(name = "Pitch")
    @NonNull
    private String pitch;

    @ColumnInfo(name = "LanguageID")
    private int languageID;

    public VocabularyEntity() {
        this.pitch = "";
        this.pronunciation = "";
        this.word = "";
    }


    public VocabularyEntity(int vocabularyID,
                            @NonNull String word,
                            @NonNull String pronunciation,
                            @NonNull String pitch,
                            int languageID) {
        this.vocabularyID = vocabularyID;
        this.word = word;
        this.pronunciation = pronunciation;
        this.pitch = pitch;
        this.languageID = languageID;
    }

    public int getVocabularyID() {
        return vocabularyID;
    }

    public void setVocabularyID(int vocabularyID) {
        this.vocabularyID = vocabularyID;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(@NonNull String pronunciation) {
        this.pronunciation = pronunciation;
    }

    @NonNull
    public String getPitch() {
        return pitch;
    }

    public void setPitch(@NonNull String pitch) {
        this.pitch = pitch;
    }

    public int getLanguageID() {
        return languageID;
    }

    public void setLanguageID(int languageID) {
        this.languageID = languageID;
    }
}

