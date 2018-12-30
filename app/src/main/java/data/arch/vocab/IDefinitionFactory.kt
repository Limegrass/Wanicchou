package data.arch.vocab

import data.room.entity.Definition
import org.jsoup.nodes.Document

interface IDefinitionFactory {
    fun getDefinition(document: Document,
                      definitionLanguageCode: String,
                      dictionaryID: Int = 0,
                      vocabularyID: Int = 0) : Definition
}