package data.arch.info.vocabulary.search

import data.arch.info.vocabulary.shared.ISourceStringStrategy
import org.jsoup.nodes.Element

internal class SanseidoSearchedWordStrategy : ISourceStringStrategy {
    /**
     * Retrieve the searched word from the html source
     * @param htmlElement the html source
     * @return the word searched for
     */
    override fun getSource(htmlElement: Element): String {
        return htmlElement.getElementById(SANSEIDO_WORD_ID).text()
    }

    companion object {
        private const val SANSEIDO_WORD_ID = "word"
    }
}