package data.vocab.lang.jp.sanseidou

import data.vocab.lang.jp.JapaneseVocabulary
import data.vocab.model.SearchProvider

object Sanseidou: SearchProvider {
    override val WEB_VIEW_CLASS: Class<*> = SanseidouSearchWebView::class.java
    override val SEARCH_RESULT_CLASS: Class<*> = SanseidouSearchResult::class.java
    override val VOCABULARY_CLASS: Class<*> = JapaneseVocabulary::class.java
}
