package data.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import data.vocab.DictionaryType;

public class VocabularyViewModel extends AndroidViewModel{
    private VocabularyRepository mRepo;
    private LiveData<List<VocabularyEntity>> mAllWords;

    public VocabularyViewModel(Application application){
        super(application);
        mRepo = new VocabularyRepository(application);
        mAllWords = mRepo.getAllWords();
    }

    public LiveData<List<VocabularyEntity>> getAllWords(){
        return mAllWords;
    }

    public void update(VocabularyEntity vocab){
        mRepo.update(vocab);
    }

    public void insert(VocabularyEntity vocab){
        mRepo.insert(vocab);
    }

    public void delete(VocabularyEntity vocab){
        mRepo.delete(vocab);
    }

    public VocabularyEntity getWord(String word, DictionaryType dictionaryType) {
        VocabularyEntity ret = null;
        try {
            ret = mRepo.getWord(word, dictionaryType);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }


}