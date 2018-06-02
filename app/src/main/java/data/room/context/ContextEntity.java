package data.room.context;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        tableName = "WordContext",
        indices = {@Index(value = "Word", unique = true)}
)
public class ContextEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ContextId")
    @NonNull
    private int id;

    @ColumnInfo(name = "Word")
    @NonNull
    private String word;

    @ColumnInfo(name = "Context")
    @NonNull
    private String context;

    public ContextEntity(){ }

    /**
     * Constructor to create a Note entry for a given word
     * @param word The word the note corresponds to.
     * @param context The note to save
     */
    public ContextEntity(String word, String context){
        this.word = word;
        this.context = context;
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
    public String getContext() {
        return context;
    }

    public void setContext(@NonNull String context) {
        this.context = context;
    }
}
