package data.room

import android.arch.persistence.room.TypeConverter

import data.room.entity.Vocabulary

class Converters {
    @TypeConverter
    fun fromVocabularyEntity(vocabulary: Vocabulary?): Int? {
        return vocabulary?.vocabularyID
    }
}
