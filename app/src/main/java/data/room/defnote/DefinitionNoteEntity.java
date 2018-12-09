package data.room.defnote;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "DefinitionNote",
        indices = {@Index(value = {"DefinitionNoteID"}, unique = true)}
)
public class DefinitionNoteEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DefinitionNoteID")
    private int definitionNoteID;

    @ColumnInfo(name = "NoteText")
    @NonNull
    private String noteText;

    @ColumnInfo(name = "DefinitionID")
    private int definitionID;


    public DefinitionNoteEntity() {
        this.noteText = "";
    }

    public DefinitionNoteEntity(int definitionNoteID,
                                @NonNull String noteText,
                                int definitionID) {
        this.definitionNoteID = definitionNoteID;
        this.noteText = noteText;
        this.definitionID = definitionID;
    }
}
