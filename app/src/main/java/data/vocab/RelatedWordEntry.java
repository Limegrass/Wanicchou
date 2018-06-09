package data.vocab;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;

public class RelatedWordEntry implements Parcelable {
    private String relatedWord;
    private DictionaryType dictionaryType;
    private String link;

    public RelatedWordEntry(String relatedWord, String dictionaryTypeString) {
        this.relatedWord = relatedWord;
        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString);
        //TODO: Handle no links for DB searches
        this.link = "#";
    }

    public RelatedWordEntry(String relatedWord, DictionaryType dictionaryType, String link) {
        this.relatedWord = relatedWord;
        this.dictionaryType = dictionaryType;
        this.link = link;
    }

    public RelatedWordEntry(String relatedWord, String dictionaryTypeString, String link) {
        this.relatedWord = relatedWord;
        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString);
        this.link = link;
    }

    public RelatedWordEntry(String relatedWord, DictionaryType dictionaryType, URL link) {
        this.relatedWord = relatedWord;
        this.dictionaryType = dictionaryType;
        this.link = link.toString();
    }

    public RelatedWordEntry(String relatedWord, String dictionaryTypeString, URL link) {
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

    private RelatedWordEntry(Parcel in) {
        relatedWord = in.readString();
        dictionaryType = JapaneseDictionaryType.fromKey(in.readString());
        link = in.readString();
    }


    public static final Creator<RelatedWordEntry> CREATOR = new Creator<RelatedWordEntry>() {
        @Override
        public RelatedWordEntry createFromParcel(Parcel in) {
            return new RelatedWordEntry(in);
        }

        @Override
        public RelatedWordEntry[] newArray(int size) {
            return new RelatedWordEntry[size];
        }
    };
}
