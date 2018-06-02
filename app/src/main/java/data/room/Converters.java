package data.room;

import android.arch.persistence.room.TypeConverter;

import data.room.voc.VocabularyEntity;
import data.vocab.DictionaryType;

public class Converters {
    /**
     * Converter for the Database given a DictionaryType parameter.
     * @param dictionaryType The dictionary type given to the query.
     * @return The toString method of the DictionaryType if it exists, else null.
     */
    @TypeConverter
    public String fromDictionaryType(DictionaryType dictionaryType){
        return dictionaryType == null ? null : dictionaryType.toString();
    }

    @TypeConverter
    public Integer fromVocabularyEntity(VocabularyEntity vocabularyEntity){
        return vocabularyEntity == null ? null : vocabularyEntity.getId();
    }
}
