package data.room.voc;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

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
        return mRepo.getWord(word, dictionaryType);
    }


}
