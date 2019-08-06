package data.room.database

import androidx.room.TypeConverter

import data.room.dbo.entity.Vocabulary

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

    @TypeConverter
    fun fromMatchType(matchType: data.enums.MatchType): Long {
        return matchType.matchTypeID
    }
    @TypeConverter
    fun toMatchType(matchTypeID : Long): data.enums.MatchType {
        return data.enums.MatchType.getMatchType(matchTypeID)
    }
}