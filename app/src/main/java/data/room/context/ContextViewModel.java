package data.room.context;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

public class ContextViewModel extends AndroidViewModel{

    private ContextRepository mRepo;

    /**
     * Constructor for the ViewModel.
     * @param application The application the database exists in.
     */
    ContextViewModel(Application application){
        super(application);
        mRepo = new ContextRepository(application);
    }

    /**
     * Inserts the note into the database.
     * @param entity The note to insert into the database
     */
    public void insert(ContextEntity entity){
        mRepo.insert(entity);
    }

    /**
     * Updates the note in the database, if it exists.
     * @param entity The note to update.
     */
    public void update(ContextEntity entity){
        mRepo.update(entity);
    }

    /**
     * Deletes a note from the database, if it exists.
     * @param entity The note to delete.
     */
    public void delete(ContextEntity entity){
        mRepo.delete(entity);
    }

    /**
     * Gets the note of a given word from the database
     * @param word The word to search for in the database.
     * @return The note of the word searched for if it exists. Else null.
     */
    public String getContextOf(String word){
        return mRepo.getContextOf(word);
    }
}
