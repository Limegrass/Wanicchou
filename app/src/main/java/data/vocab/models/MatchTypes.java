package data.vocab.models;

import data.vocab.jp.search.sanseido.SanseidoMatchType;

/**
 * Class listing all the available match types
 */
final public class MatchTypes {
    private static final Class[] TYPES = {SanseidoMatchType.class};

    /**
     * Gets the MatchType given a string representing that match type,
     * if it is valid within all the types defined.
     * @param matchType a string of the match type
     * @return a MatchType instance corresponding to the input string
     */
    public static MatchType getMatchType(String matchType){
        for (Class type : TYPES) {
            try{
                return (MatchType) Enum.valueOf(type, matchType);
            }
            catch (IllegalArgumentException ex){
            }
        }
        return null;
    }
}
