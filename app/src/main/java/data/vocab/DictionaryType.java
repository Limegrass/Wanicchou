package data.vocab;

/**
 * Created by Limegrass on 5/9/2018.
 */

public enum DictionaryType {
    JJ, JE, EJ;

    public String toJapaneseDictionaryType(){
        String eng = this.toString();
        switch (eng){
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

    public static DictionaryType fromString(String key){
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

    public static DictionaryType fromSanseidoKey(String key){
        switch (key){
            case "DailyJJ":
                return JJ;
            case "DailyJE":
                return JE;
            case "DailyEJ":
                return EJ;
            default:
                return null;
        }
    }

    public static DictionaryType fromSanseidoKanji(String key){
        switch (key){
            case "【国語】":
                return JJ;
            case "【和英】":
                return JE;
            case "【英和】":
                return EJ;
            default:
                return null;
        }
    }
}
