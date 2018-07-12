package data.vocab.models;

import data.vocab.jp.search.sanseido.SanseidoMatchType;

final public class MatchTypes {
    public static final Class[] TYPES = {SanseidoMatchType.class};

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
