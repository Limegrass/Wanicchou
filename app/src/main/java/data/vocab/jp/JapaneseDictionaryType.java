package data.vocab.jp;

import data.vocab.models.DictionaryType;

/**
 * Created by Limegrass on 5/9/2018.
 */

public enum JapaneseDictionaryType implements DictionaryType {
    JJ, JE, EJ;

    /**
     * Converts to the Japanese name of the JapaneseDictionaryType.
     * @return The Japanese names of the dictionary type.
     */
    public String toDisplayText(){
        String key = this.toString();
        switch (key){
            case "JJ":
                return "国語";
            case "JE":
                return "和英";
            case "EJ":
                return "英和";
            default:
                return null;
        }
    }

    public String toKey(){
        return this.toString();
    }

    /**
     * Converts from the Japanese dictionary type to the enum type.
     * @param key The Japanese dictionary type key.
     * @return A JapaneseDictionaryType corresponding to the key, if it exists. Else, null.
     */
    public static DictionaryType fromJapaneseDictionaryKanji(String key){
        switch (key){
            case "国語":
                return JJ;
            case "和英":
                return JE;
            case "英和":
                return EJ;
            default:
                return null;
        }
    }

    /**
     * Converts a string representation of the dictionary type if it exists.
     * @param key The string representation of the dictionary type.
     * @return A JapaneseDictionaryType corresponding to the key, if it exists. Else, null.
     */
    public static DictionaryType fromKey(String key){
        switch (key){
            case "JJ":
                return JJ;
            case "JE":
                return JE;
            case "EJ":
                return EJ;
            default:
                return null;
        }
    }
}
