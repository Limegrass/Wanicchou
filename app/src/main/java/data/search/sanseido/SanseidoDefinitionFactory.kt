package data.search.sanseido

import data.room.entity.Definition
import data.arch.vocab.IDefinitionFactory

object SanseidoDefinitionFactory : IDefinitionFactory {
    private const val MULTIPLE_DEFINITION_REGEX = "▼"
    private const val MULTIPLE_DEFINITION_SEPARATOR = "\n▼"

    override fun getDefinition (definitionLanguageCode: String,
                                definitionSource: String,
                                dictionaryID: Long,
                                vocabularyID: Long) : Definition {
        val definition = formatDefinitionSource(definitionSource)
        return Definition(definition,
                          definitionLanguageCode,
                          dictionaryID,
                          vocabularyID)
    }

    /**
     * A helper method to isolate the source text of the definition of the word searched.
     * @param definitionSource the raw string of the definition
     * @return the raw definition source
     */
    private fun formatDefinitionSource(definitionSource : String): String {
        // The definition is in a further div, single child
        var formattedDefinition = definitionSource
        //TODO: FIX REGEX
        formattedDefinition = formattedDefinition.replace(MULTIPLE_DEFINITION_REGEX.toRegex(),
                                                          MULTIPLE_DEFINITION_SEPARATOR)
//        formattedDefinition = formattedDefinition.replaceFirst(MULTIPLE_DEFINITION_REGEX.toRegex(),
//                                                               MULTIPLE_DEFINITION_SEPARATOR)

        return formattedDefinition
    }


}