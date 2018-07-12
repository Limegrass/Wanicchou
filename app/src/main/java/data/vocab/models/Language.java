package data.vocab.models;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.jp.JapaneseVocabulary;

/**
 * Enum listing all available languages.
 */
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
}
