package data.room.notes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


//TODO: Some way to avoid having to save the word and make it relational to the Vocab DB
// Given that the Vocab DB can have 2 entries for a word (for two different dictionaries)
/**
 * Notes entity to save notes for a particular word
 */
@Entity(
        tableName = "Notes",
        indices = {@Index(value = {"Word"}, unique = true)}
)
public class NoteEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "NoteId")
    @NonNull
    private int id;

    @ColumnInfo(name = "Word")
    @NonNull
    private String word;

    @ColumnInfo(name = "Note")
    @NonNull
    private String note;

    public NoteEntity(){ }

    /**
     * Constructor to create a Note entry for a given word
     * @param word The word the note corresponds to.
     * @param note The note to save
     */
    public NoteEntity(String word, String note){
        this.word = word;
        this.note = note;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getNote() {
        return note;
    }

    public void setNote(@NonNull String note) {
        this.note = note;
    }
}
