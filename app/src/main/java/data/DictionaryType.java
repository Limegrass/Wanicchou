package data;

/**
 * Created by Limegrass on 5/9/2018.
 */

public enum DictionaryType {
    JJ ("JJ"),
    JE ("JE"),
    EJ ("EJ");

    private String type;

    DictionaryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
