package data.room.vocnote;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "VocabularyNote",
        indices = {@Index(value = {"VocabularyNoteID"}, unique = true)}
)
public class VocabularyNoteEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VocabularyNoteID")
    private int vocabularyNoteID;

    @ColumnInfo(name = "NoteText")
    @NonNull
    private String noteText;

    @ColumnInfo(name = "VocabularyID")
    private int vocabularyID;

    public VocabularyNoteEntity() {
        this.noteText = "";
    }


    public VocabularyNoteEntity(int vocabularyNoteID, @NonNull String noteText, int vocabularyID){
        this.vocabularyNoteID = vocabularyNoteID;
        this.noteText = noteText;
        this.vocabularyID = vocabularyID;
    }

}
