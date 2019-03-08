package data.room.database

import androidx.room.TypeConverter

import data.room.entity.Vocabulary

class Converters {
    @TypeConverter
    fun fromVocabularyEntity(vocabulary: Vocabulary?): Long? {
        return vocabulary?.vocabularyID
    }
}
