package data.vocab.models;

import data.vocab.jp.JapaneseDictionaryType;

final public class DictionaryTypes {
    public static final Class[] TYPES = {JapaneseDictionaryType.class};

    public static DictionaryType getDictionaryType(String dicType){
        for (Class type : TYPES) {
            try{
                return (DictionaryType) Enum.valueOf(type, dicType);
            }
            catch (IllegalArgumentException ex){
            }
        }
        return null;
    }

    public static DictionaryType[] getAllDictionaryTypeForLanguage(Language language){
        return (DictionaryType[]) language.getDictionaryTypeClass().getEnumConstants();
    }
}
