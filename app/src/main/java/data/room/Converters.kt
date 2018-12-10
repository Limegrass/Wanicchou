package data.room

import android.arch.persistence.room.TypeConverter

import data.room.entity.Vocabulary
import data.vocab.models.DictionaryType

class Converters {
    /**
     * Converter for the Database given a JapaneseDictionaryType parameter.
     * @param dictionaryType The dictionary type given to the query.
     * @return The toString method of the JapaneseDictionaryType if it exists, else null.
     */
    @TypeConverter
    fun fromDictionaryType(dictionaryType: DictionaryType): String {
        return dictionaryType.toKey()
    }

    @TypeConverter
    fun fromVocabularyEntity(vocabulary: Vocabulary?): Int? {
        return vocabulary?.vocabularyID
    }
}
