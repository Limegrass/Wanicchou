package data.room.context;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

public class ContextViewModel extends AndroidViewModel{

    private ContextRepository mRepo;

    /**
     * Constructor for the ViewModel.
     * @param application The application the database exists in.
     */
    public ContextViewModel(Application application){
        super(application);
        mRepo = new ContextRepository(application);
    }

    public boolean insertNewContext(String word){
        if(mRepo.getContextEntityOf(word) != null){
            return false;
        }
        ContextEntity contextEntity = new ContextEntity(word, "");
        mRepo.insert(contextEntity);
        return true;
    }

    /**
     * Updates the context in the DB for the given word
     * @param word The word whose context should be updated.
     * @param updatedContext The updated context
     */
    public void updateContext(String word, String updatedContext){
        ContextEntity entity = mRepo.getContextEntityOf(word);
        entity.setContext(updatedContext);
        mRepo.update(entity);
    }

    /**
     * Deletes the linguistic context saved for a word, if it exists.
     * @param word The word whose linguistic context should be deleted.
     */
    public void delete(String word){
        ContextEntity contextEntity = mRepo.getContextEntityOf(word);
        mRepo.delete(contextEntity);
    }

    /**
     * Gets the linguistic context of a given word from the database
     * @param word The word to search for in the database.
     * @return The linguistic context of the word searched if it exists. Else null.
     */
    public String getContextOf(String word){
        return mRepo.getContextOf(word);
    }
}
