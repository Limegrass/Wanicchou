package data.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import data.vocab.JapaneseVocabulary;

@Entity(
        tableName = "VocabularyWords",
        indices = {@Index(value = {"Word", "DictionaryType"}, unique = true)}
)
public class VocabularyEntity {
    @PrimaryKey(autoGenerate = true)
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


    @ColumnInfo(name = "Notes")
    @NonNull
    private String notes;

    @ColumnInfo(name = "Context")
    @NonNull
    private String wordContext;

    public VocabularyEntity(){}

    public VocabularyEntity(JapaneseVocabulary vocabulary, String notes, String wordContext){
        word = vocabulary.getWord();
        definition = vocabulary.getDefintion();
        reading = vocabulary.getReading();
        dictionaryType = vocabulary.getDictionaryType().toString();
        pitch = vocabulary.getPitch();

        this.notes = notes;
        this.wordContext = wordContext;
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

    @NonNull
    public String getNotes() {
        return notes;
    }

    public void setNotes(@NonNull String notes) {
        this.notes = notes;
    }

    @NonNull
    public String getWordContext() {
        return wordContext;
    }

    public void setWordContext(@NonNull String wordContext) {
        this.wordContext = wordContext;
    }
}

