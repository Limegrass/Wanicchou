package data.arch.search.jsoup

import data.arch.models.IDictionaryEntry
import data.enums.Language
import org.jsoup.nodes.Element

interface IJsoupDictionaryEntryFactory {
    fun getDictionaryEntries(element : Element,
                           vocabularyLanguage: Language,
                           definitionLanguage: Language) : List<IDictionaryEntry>
}