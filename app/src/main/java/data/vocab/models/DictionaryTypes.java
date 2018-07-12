package data.vocab.models;

import android.support.annotation.Nullable;

import data.vocab.jp.JapaneseDictionaryType;

final public class DictionaryTypes {
    private static final Class[] TYPES = {JapaneseDictionaryType.class};

    /**
     * Returns a DictionaryType given a string of the type's name
     * @param dicType a string of the name of a dictionary type
     * @return a DictionaryType instance if it is a valid DictionaryType among the list, else null
     */
    @Nullable
    public static DictionaryType getDictionaryType(String dicType){
        for (Class type : TYPES) {
            try{
                return (DictionaryType) Enum.valueOf(type, dicType);
            }
            catch (IllegalArgumentException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns a list of DictionaryTypes for a certain language
     * @param language the language to find all dictionary types for
     * @return an array of DictionaryTypes for the language desired if it exists, else null
     */
    @Nullable
    public static DictionaryType[] getAllDictionaryTypeForLanguage(Language language){
        Class dictionaryTypeClass = language.getDictionaryTypeClass();
        if (dictionaryTypeClass != null){
            return (DictionaryType[]) language.getDictionaryTypeClass().getEnumConstants();
        }
        else {
            return null;
        }
    }
}
