package data.room.notes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

public class NoteViewModel extends AndroidViewModel{
    private NoteRepository mRepo;

    /**
     * Constructor for the ViewModel.
     * @param application The application the database exists in.
     */
    public NoteViewModel(Application application){
        super(application);
        mRepo = new NoteRepository(application);
    }

    /**
     * Inserts a word into the database with an empty note if a
     * note does not already exist.
     * @param word The word to insert a new entity for.
     * @return True if the word was added to the DB, false if it already existed.
     */
    public boolean insertNewNote(String word){
        //If the word already exists
        if(mRepo.getNoteEntityOf(word) != null){
            return false;
        }
        NoteEntity noteEntity = new NoteEntity(word, "");
        mRepo.insert(noteEntity);
        return true;
    }

    public void updateNote(String word, String updatedNotes){
        NoteEntity entity = mRepo.getNoteEntityOf(word);
        entity.setNote(updatedNotes);
        mRepo.update(entity);
    }

    /**
     * Deletes the notes of a word, if it exists.
     * @param word The word whose notes should be deleted.
     */
    public void delete(String word){
        NoteEntity noteEntity = mRepo.getNoteEntityOf(word);
        mRepo.delete(noteEntity);
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
