package data.search.jsoup

import data.models.IDictionaryEntry
import data.enums.Language
import org.jsoup.nodes.Element

interface IJsoupDictionaryEntryFactory {
    fun getDictionaryEntries(element : Element,
                           vocabularyLanguage: Language,
                           definitionLanguage: Language) : List<IDictionaryEntry>
}