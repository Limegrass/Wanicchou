package data.vocab.models;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.jp.JapaneseVocabulary;

public enum Language {
    JAPANESE;
    public Class getDictionaryTypeClass(){
        switch(this){
            case JAPANESE:
                return JapaneseDictionaryType.class;
            default:
                return null;
        }
    }

    public Class getVocabularyClass(){
        switch(this){
            case JAPANESE:
                return JapaneseVocabulary.class;
            default:
                return null;
        }
    }
}
