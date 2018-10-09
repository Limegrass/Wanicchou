package data.vocab;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;

public class WordListEntry implements Parcelable {
    private String relatedWord;
    private DictionaryType dictionaryType;
    private String link;

    private static String NULL = null;

    public WordListEntry(String relatedWord, String dictionaryTypeString) {
        this.relatedWord = relatedWord;
        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString);
        //TODO: Handle no links for DB searches
        this.link = "";
    }

    public WordListEntry(String relatedWord, DictionaryType dictionaryType, String link) {
        this.relatedWord = relatedWord;
        this.dictionaryType = dictionaryType;
        this.link = link;
    }

    public WordListEntry(String relatedWord, String dictionaryTypeString, String link) {
        this.relatedWord = relatedWord;
        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString);
        this.link = link;
    }

    public WordListEntry(String relatedWord, DictionaryType dictionaryType, URL link) {
        this.relatedWord = relatedWord;
        this.dictionaryType = dictionaryType;
        this.link = link.toString();
    }

    public WordListEntry(String relatedWord, String dictionaryTypeString, URL link) {
        this.relatedWord = relatedWord;
        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString);
        this.link = link.toString();
    }


    public String getRelatedWord() {
        return relatedWord;
    }

    public void setRelatedWord(String relatedWord) {
        this.relatedWord = relatedWord;
    }

    public DictionaryType getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(JapaneseDictionaryType dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // PARCELABLE IMPLEMENTATION
    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(relatedWord);
        parcel.writeString(dictionaryType.toString());
        parcel.writeString(link);
    }

    private WordListEntry(Parcel in) {
        relatedWord = in.readString();
        dictionaryType = JapaneseDictionaryType.fromKey(in.readString());
        link = in.readString();
    }


    public static final Creator<WordListEntry> CREATOR = new Creator<WordListEntry>() {
        @Override
        public WordListEntry createFromParcel(Parcel in) {
            return new WordListEntry(in);
        }

        @Override
        public WordListEntry[] newArray(int size) {
            return new WordListEntry[size];
        }
    };
}
