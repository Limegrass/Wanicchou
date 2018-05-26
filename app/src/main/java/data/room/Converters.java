package data.room;

import android.arch.persistence.room.TypeConverter;

import data.vocab.DictionaryType;

public class Converters {
    @TypeConverter
    public String fromDictionaryType(DictionaryType dictionaryType){
        return dictionaryType == null ? null : dictionaryType.toString();
    }

    @TypeConverter
    public DictionaryType stringToDictionaryType(String sanseidoKey){
        return sanseidoKey == null ? null : DictionaryType.fromSanseidoKey(sanseidoKey);
    }

}
