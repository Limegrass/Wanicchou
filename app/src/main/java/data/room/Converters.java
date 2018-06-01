package data.room;

import android.arch.persistence.room.TypeConverter;

import data.vocab.DictionaryType;

public class Converters {
    @TypeConverter
    public String fromDictionaryType(DictionaryType dictionaryType){
        return dictionaryType == null ? null : dictionaryType.toString();
    }

    @TypeConverter
    public DictionaryType stringToDictionaryType(String key){
        return key == null ? null : DictionaryType.fromString(key);
    }

}
