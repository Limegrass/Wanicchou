package data.models

import data.enums.Language

class Vocabulary (override val word: String,
                  override val pronunciation: String,
                  override val pitch: String,
                  override val language: Language) : IVocabulary {
    override fun toString(): String {
        return word
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is IVocabulary){
            return false
        }
        return this.word == other.word
                && this.pronunciation == other.pronunciation
                && this.language == other.language
                && this.pitch == other.pitch
    }

    override fun hashCode(): Int {
        return word.hashCode() xor
                pronunciation.hashCode() xor
                language.hashCode() xor
                pitch.hashCode()

    }
}