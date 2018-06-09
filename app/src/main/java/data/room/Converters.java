package data.room;

import android.arch.persistence.room.TypeConverter;

import data.room.voc.VocabularyEntity;
import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;

public class Converters {
    /**
     * Converter for the Database given a JapaneseDictionaryType parameter.
     * @param dictionaryType The dictionary type given to the query.
     * @return The toString method of the JapaneseDictionaryType if it exists, else null.
     */
    @TypeConverter
    public String fromDictionaryType(DictionaryType dictionaryType){
        return dictionaryType == null ? null : dictionaryType.toKey();
    }

    @TypeConverter
    public Integer fromVocabularyEntity(VocabularyEntity vocabularyEntity){
        return vocabularyEntity == null ? null : vocabularyEntity.getId();
    }
}
