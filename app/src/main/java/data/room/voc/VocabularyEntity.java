package data.room.voc;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import data.vocab.JapaneseVocabulary;

/**
 * Vocabulary Entry for the Room Persistence Library, for Words and their definitions.
 */
@Entity(
        tableName = "VocabularyWords",
        indices = {@Index(value = {"Word", "DictionaryType"}, unique = true)}
)
public class VocabularyEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyId")
    @NonNull
    private int id;

    @ColumnInfo(name = "Word")
    @NonNull
    private String word;

    @ColumnInfo(name = "DictionaryType")
    @NonNull
    private String dictionaryType;

    @ColumnInfo(name = "Reading")
    @NonNull
    private String reading;

    @ColumnInfo(name = "Definition")
    @NonNull
    private String definition;

    @ColumnInfo(name = "Pitch")
    @NonNull
    private String pitch;


    /**
     * Empty constructor for the RPM.
     */
    public VocabularyEntity(){}

    // TODO?: Maybe consolidate this and the JapaneseVocab class
    // Having everything in one class vs having a class that cleans
    // and this entity class
    /**
     * Constructor given a vocabulary word, along with notes and context for the vocab.
     * @param vocabulary The vocabulary word to construct the entity from.
     */
    public VocabularyEntity(JapaneseVocabulary vocabulary){
        word = vocabulary.getWord();
        definition = vocabulary.getDefintion();
        reading = vocabulary.getReading();
        dictionaryType = vocabulary.getDictionaryType().toString();
        pitch = vocabulary.getPitch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(@NonNull String dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    @NonNull
    public String getReading() {
        return reading;
    }

    public void setReading(@NonNull String reading) {
        this.reading = reading;
    }

    @NonNull
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(@NonNull String definition) {
        this.definition = definition;
    }

    @NonNull
    public String getPitch() {
        return pitch;
    }

    public void setPitch(@NonNull String pitch) {
        this.pitch = pitch;
    }

}

