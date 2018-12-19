package data.vocab.model

/**
 * Interface to program a language's dictionaryEntry entry to
 */
class DictionaryEntry(val dictionary: String,
                      val wordLanguageCode: String,
                      val word: String,
                      val pronunciation: String,
                      val pitch: String,
                      val definitionLanguageCode: String,
                      val definition: String) {

    /**
     * Compares by word, pronunciation, definition, and pitch
     * @param other another JapaneseVocabulary instance
     * @return true if word, pronunciation, definition, and pitch are all the same value
     */
    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is DictionaryEntry) {
            return false
        }
        val otherVocabulary: DictionaryEntry = other

        //Maybe not include definition in case of different site definitions or formatting.
        return (word == otherVocabulary.word
                && wordLanguageCode == otherVocabulary.wordLanguageCode
                && definitionLanguageCode == otherVocabulary.definitionLanguageCode
                && pronunciation == otherVocabulary.pronunciation
                && definition == otherVocabulary.definition
                && pitch == otherVocabulary.pitch)
    }

    /**
     * Hashes by word, pronunciation, definition, and pitch
     * @return a hashcode for the dictionaryEntry word
     */
    override fun hashCode(): Int {
        var hash = 17
        hash = 31 * hash + word.hashCode()
        hash = 31 * hash + wordLanguageCode.hashCode()
        hash = 31 * hash + definitionLanguageCode.hashCode()
        hash = 31 * hash + pronunciation.hashCode()
        hash = 31 * hash + definition.hashCode()
        hash = 31 * hash + pitch.hashCode()
        return hash
    }
}
