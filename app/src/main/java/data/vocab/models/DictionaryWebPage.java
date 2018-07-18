package data.vocab.models;


import org.jsoup.nodes.Document;

import data.vocab.WordListEntry;
import data.vocab.jp.JapaneseDictionaryType;

/**
 * Interface for WebViews of a SearchProvider
 */
public interface DictionaryWebPage {

    Vocabulary getVocabulary();
    SearchResult getSearch();
    DictionaryType getCurrentDictionaryType();
    void setCurrentDictionaryType(JapaneseDictionaryType dictionaryType);
    void navigateRelatedWord(WordListEntry relatedWord);
    String getUrl();

    Document getHtmlDocument();

}
