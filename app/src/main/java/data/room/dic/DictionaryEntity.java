package data.room.dic;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "Dictionary",
        indices = {@Index(value = {"DictionaryID"}, unique = true)}
)
public class DictionaryEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DictionaryID")
    private int dictionaryID;

    @ColumnInfo(name = "DictionaryName")
    @NonNull
    private String dictionaryName;

    public DictionaryEntity() {
        this.dictionaryName = "";
    }

    public DictionaryEntity(int dictionaryID, @NonNull String dictionaryName){
        this.dictionaryID = dictionaryID;
        this.dictionaryName = dictionaryName;
    }

    @NonNull
    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(@NonNull String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public int getDictionaryID() {
        return dictionaryID;
    }

    public void setDictionaryID(int dictionaryID) {
        this.dictionaryID = dictionaryID;
    }
}

