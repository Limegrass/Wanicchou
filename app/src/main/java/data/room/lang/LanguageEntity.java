package data.room.lang;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "Language",
        indices = {@Index(value = {"LanguageID"}, unique = true)}
)
public class LanguageEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LanguageID")
    private int languageID;

    @ColumnInfo(name = "CultureCode")
    private String cultureCode;

    public LanguageEntity() {
        this.cultureCode = "";
    }

    public LanguageEntity(int languageID, String cultureCode) {
        this.languageID = languageID;
        this.cultureCode = cultureCode;
    }

    public String getCultureCode() {
        return cultureCode;
    }

    public void setCultureCode(@NonNull String cultureCode) {
        this.cultureCode = cultureCode;
    }

    public int getLanguageID() {
        return languageID;
    }

    public void setLanguageID(int languageID) {
        this.languageID = languageID;
    }
}
