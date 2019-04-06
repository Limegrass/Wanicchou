package data.arch.info.definition

import data.room.entity.Definition
import org.jsoup.nodes.Document

internal class SanseidoDefinitionStrategy : IDefinitionStrategy {
    companion object {
        private const val SANSEIDO_WORD_DEFINITION_ID = "wordBody"
        private const val MULTIPLE_DEFINITION_REGEX = "▼"
        private const val MULTIPLE_DEFINITION_SEPARATOR = "\n▼"
    }

    override fun get(htmlDocument: Document,
                     definitionLanguageID: Long,
                     dictionaryID: Long,
                     vocabularyID: Long) : Definition {
        val definitionSource = getDefinitionSource(htmlDocument)
        val definition = formatDefinitionSource(definitionSource)
        return Definition(definition,
                definitionLanguageID,
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

        return formattedDefinition.trim()
    }

    private fun getDefinitionSource(html : Document) : String {
        val definitionParentElement = html.getElementById(SANSEIDO_WORD_DEFINITION_ID)
        return if (definitionParentElement.children().size > 0) {
            definitionParentElement.child(0).text()
        }
        else {
            ""
        }
    }

}