package data.room.tag;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "Tag",
        indices = {@Index(value = {"TagID"}, unique = true)}
)
public class TagEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TagID")
    private int tagID;

    @ColumnInfo(name = "TagText")
    @NonNull
    private String tagText;

    public TagEntity() {
        this.tagText = "";
    }

    public TagEntity(int tagID, @NonNull String tagText) {
        this.tagID = tagID;
        this.tagText = tagText;
    }

    @NonNull
    public String getTagText() {
        return tagText;
    }

    public void setTagText(@NonNull String tagText) {
        this.tagText = tagText;
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }
}
