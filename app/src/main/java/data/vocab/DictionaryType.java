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
                return "";
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
                return JJ;
        }
    }

}
