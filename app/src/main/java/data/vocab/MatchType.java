package data.vocab;

/**
 * Created by Limegrass on 5/9/2018.
 */

public enum MatchType {
    FORWARDS, EXACT, BACKWARDS, PARTIAL, FULL_TEXT;

    public String searchKey(){
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

    public static MatchType fromString(String key){
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
}
