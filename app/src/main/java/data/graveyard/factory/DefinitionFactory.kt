package data.graveyard.factory

import data.room.entity.Definition
import data.graveyard.DictionaryEntry

class DefinitionFactory {
    fun getDefinition(dictionaryEntry: DictionaryEntry, dictionaryID: Int, vocabularyID: Int): Definition {
        return Definition(dictionaryEntry.definition,
                          dictionaryEntry.definitionLanguageCode,
                          dictionaryID,
                          vocabularyID)
    }
}