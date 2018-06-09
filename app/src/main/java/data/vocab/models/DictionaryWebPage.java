package data.vocab.models;


import data.vocab.jp.JapaneseDictionaryType;

public interface DictionaryWebPage {

    Vocabulary getVocabulary();
    Search getSearch();
    DictionaryType getCurrentDictionaryType();
    void setCurrentDictionaryType(JapaneseDictionaryType dictionaryType);
    void navigateRelatedWordLinks(int index);



}
