package data.room.notes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

public class NoteViewModel extends AndroidViewModel{
    private NoteRepository mRepo;

    /**
     * Constructor for the ViewModel.
     * @param application The application the database exists in.
     */
    NoteViewModel(Application application){
        super(application);
        mRepo = new NoteRepository(application);
    }

    /**
     * Inserts the note into the database.
     * @param entity The note to insert into the database
     */
    public void insert(NoteEntity entity){
        mRepo.insert(entity);
    }

    /**
     * Updates the note in the database, if it exists.
     * @param entity The note to update.
     */
    public void update(NoteEntity entity){
        mRepo.update(entity);
    }

    /**
     * Deletes a note from the database, if it exists.
     * @param entity The note to delete.
     */
    public void delete(NoteEntity entity){
        mRepo.delete(entity);
    }

    /**
     * Gets the note of a given word from the database
     * @param word The word to search for in the database.
     * @return The note of the word searched for if it exists. Else null.
     */
    public String getNoteOf(String word){
        return mRepo.getNoteOf(word);
    }
}
