package data.vocab.jp.search.sanseido;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.jp.JapaneseVocabulary;
import data.vocab.models.Language;
import data.vocab.models.SearchProvider;

final public class Sanseido extends SearchProvider {
    static {
        LANGUAGE = Language.JAPANESE;
        DICTIONARY_TYPE_CLASS = JapaneseDictionaryType.class;
        MATCH_TYPE_CLASS = SanseidoMatchType.class;
        WEB_VIEW_CLASS = SanseidoSearchWebView.class;
        SEARCH_CLASS = SanseidoSearchResult.class;
        VOCABULARY_CLASS = JapaneseVocabulary.class;
    }

    public Sanseido(){}
}
