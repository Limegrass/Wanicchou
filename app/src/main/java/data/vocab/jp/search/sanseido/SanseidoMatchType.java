package data.vocab.jp.search.sanseido;

import data.vocab.models.MatchType;

/**
 * Created by Limegrass on 5/9/2018.
 */

public enum SanseidoMatchType implements MatchType {
    FORWARDS, EXACT, BACKWARDS, PARTIAL, FULL_TEXT;

    /**
     * Converts the match type to its corresponding key for a Sanseido search.
     * @return A string of the search key for Sanseido Searches.
     */
    public String toKey(){
        switch (this){
            case FORWARDS:
                return "0";
            case EXACT:
                return "1";
            case BACKWARDS:
                return "2";
            case FULL_TEXT:
                return "3";
            case PARTIAL:
                return "5";
            default:
                return null;
        }
    }


    /**
     * Converts the Sanseido search key into a SanseidoMatchType enum object, for clarity.
     * @param key The Sanseido search key.
     * @return The SanseidoMatchType for the Sanseido key if it is a valid key, else null.
     */
    public static SanseidoMatchType fromKey(String key){
        switch (key) {
            case "0":
                return FORWARDS;
            case "1":
                return EXACT;
            case "2":
                return BACKWARDS;
            case "3":
                return FULL_TEXT;
            case "5":
                return PARTIAL;
            default:
                return null;
        }

    }

    //(YourInterface) Enum.valueOf(clazz, stringName)
}
