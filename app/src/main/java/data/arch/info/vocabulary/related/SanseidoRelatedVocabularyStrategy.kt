package data.arch.info.vocabulary.related

import data.arch.info.vocabulary.shared.SanseidoVocabularyStrategy
import data.room.entity.Vocabulary
import org.jsoup.nodes.Element

internal class SanseidoRelatedVocabularyStrategy
    : IRelatedVocabularyStrategy{

    companion object {
        private const val RELATED_WORDS_VOCAB_INDEX = 1
        private const val RELATED_WORDS_TABLE_INDEX = 0
        private const val RELATED_WORDS_PAGER_ID = "_ctl0_ContentPlaceHolder1_ibtGoNext"
    }

    override fun getRelatedVocabulary(htmlElement: Element,
                                      wordLanguageID : Long): List<Vocabulary> {
        val relatedWordEntries = ArrayList<Vocabulary>()
        val table = htmlElement.select("table")[RELATED_WORDS_TABLE_INDEX]
        val rows = table.select("tr")
        val strategy = SanseidoVocabularyStrategy()

        for (row in rows) {
            val columns = row.select("td")
            val tableEntry = columns[RELATED_WORDS_VOCAB_INDEX].text()
            val relatedVocabulary = strategy.get(tableEntry, wordLanguageID)
            relatedWordEntries.add(relatedVocabulary)
        }

        return relatedWordEntries
    }


}