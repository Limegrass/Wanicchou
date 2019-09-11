package room.database

import androidx.room.TypeConverter

import room.dbo.entity.Vocabulary

class Converters {
    @TypeConverter
    fun fromVocabularyEntity(vocabulary: Vocabulary?): Long? {
        return vocabulary?.vocabularyID
    }

    @TypeConverter
    fun fromLanguage(language: data.enums.Language): Long {
        return language.languageID
    }
    @TypeConverter
    fun toLanguage(languageID : Long): data.enums.Language {
        return data.enums.Language.getLanguage(languageID)
    }

    @TypeConverter
    fun fromDictionary(dictionary: data.enums.Dictionary): Long {
        return dictionary.dictionaryID
    }
    @TypeConverter
    fun toDictionary(dictionaryID : Long): data.enums.Dictionary {
        return data.enums.Dictionary.getDictionary(dictionaryID)
    }
}