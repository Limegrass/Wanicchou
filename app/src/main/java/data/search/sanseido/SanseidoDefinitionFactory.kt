package data.search.sanseido

import data.room.entity.Definition
import data.arch.vocab.IDefinitionFactory
import org.jsoup.nodes.Document

object SanseidoDefinitionFactory : IDefinitionFactory {
    private const val SANSEIDO_WORD_DEFINITION_ID = "wordBody"
    private const val MULTIPLE_DEFINITION_REGEX = "▼"
    private const val MULTIPLE_DEFINITION_SEPARATOR = "\n▼"

    override fun getDefinition (document: Document,
                                definitionLanguageCode: String,
                                dictionaryID: Int,
                                vocabularyID: Int) : Definition {
        val definition = findDefinitionSource(document)?.trim() ?: ""
        return Definition(definition,
                          definitionLanguageCode,
                          dictionaryID,
                          vocabularyID)
    }

    /**
     * A helper method to isolate the source text of the definition of the word searched.
     * @param html the jsoup html document tree.
     * @return the raw definition source
     */
    private fun findDefinitionSource(html: Document): String? {
        val definitionParentElement = html.getElementById(SANSEIDO_WORD_DEFINITION_ID)
        // The definition is in a further div, single child
        var definitionSource : String? = null

        if (definitionParentElement.children().size > 0) {
            definitionSource = definitionParentElement.child(0).text()
            //TODO: FIX REGEX
            definitionSource = definitionSource.replace(MULTIPLE_DEFINITION_REGEX.toRegex(),
                    MULTIPLE_DEFINITION_SEPARATOR)
            definitionSource = definitionSource.replaceFirst(MULTIPLE_DEFINITION_REGEX.toRegex(),
                    MULTIPLE_DEFINITION_SEPARATOR)
        }

        return definitionSource
    }

}