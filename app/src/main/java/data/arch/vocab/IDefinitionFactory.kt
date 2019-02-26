package data.arch.vocab

import data.room.entity.Definition
import org.jsoup.nodes.Document

interface IDefinitionFactory {
    fun getDefinition(definitionLanguageCode: String,
                      definitionSource: String,
                      dictionaryID: Long = 0,
                      vocabularyID: Long = 0) : Definition
}