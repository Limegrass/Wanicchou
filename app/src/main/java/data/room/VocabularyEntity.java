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
    public int id;

    @ColumnInfo(name = "Word")
    @NonNull
    public String mWord;

    @ColumnInfo(name = "DictionaryType")
    @NonNull
    public String mDictionaryType;

    @ColumnInfo(name = "Reading")
    @NonNull
    public String mReading;

    @ColumnInfo(name = "Definition")
    @NonNull
    public String mDefinition;

    @ColumnInfo(name = "Pitch")
    @NonNull
    public String mPitch;

    @ColumnInfo(name = "Notes")
    @NonNull
    public String mNotes;

    @ColumnInfo(name = "Context")
    @NonNull
    public String mWordContext;

    public VocabularyEntity(){}

    public VocabularyEntity(JapaneseVocabulary vocabulary, String notes, String wordContext){
        mWord = vocabulary.getWord();
        mDefinition = vocabulary.getDefintion();
        mReading = vocabulary.getReading();
        mDictionaryType = vocabulary.getDictionaryType().toString();
        mPitch = vocabulary.getPitch();

        mNotes = notes;
        mWordContext = wordContext;
    }
}

