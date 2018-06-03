package data.room.voc;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import data.vocab.DictionaryType;

/**
 * Vocab ViewModel
 */
public class VocabularyViewModel extends AndroidViewModel{
    private VocabularyRepository mRepo;

    /**
     * Constructor for the ViewModel.
     * @param application The application the database exists in.
     */
    public VocabularyViewModel(Application application){
        super(application);
        mRepo = new VocabularyRepository(application);
    }


    /**
     * Updates the word in the database.
     * @param vocab The word entry to update in the database.
     */
    public void update(VocabularyEntity vocab){
        mRepo.update(vocab);
    }

    /**
     * Inserts a word into the database.
     * @param vocab The word to insert into the database.
     */
    public void insert(VocabularyEntity vocab){
        mRepo.insert(vocab);
    }

    /**
     * Deletes the word from the database, if it exists.
     * @param vocab The word entry to delete from the database, if it exists.
     */
    public void delete(VocabularyEntity vocab){
        mRepo.delete(vocab);
    }

    /**
     * Queries for the word in the database in the given dictionary type.
     * @param word The word to search for in the database.
     * @param dictionaryType The dictionary type of the word-definition pair.
     * @return The entry in the database, if it exists. Else, null.
     */
    public VocabularyEntity getWord(String word, DictionaryType dictionaryType) {
        return mRepo.getWord(word, dictionaryType);
    }


}
