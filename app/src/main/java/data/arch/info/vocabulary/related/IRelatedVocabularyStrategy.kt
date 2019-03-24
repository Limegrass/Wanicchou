package data.arch.info.vocabulary.related

import data.room.entity.Vocabulary
import org.jsoup.nodes.Element

internal interface IRelatedVocabularyStrategy {
    fun getRelatedVocabulary(htmlElement : Element,
                             wordLanguageCode : String): List<Vocabulary>
}