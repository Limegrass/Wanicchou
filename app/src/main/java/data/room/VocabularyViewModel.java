package data.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

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

    public void insert(VocabularyEntity vocab){
        mRepo.insert(vocab);
    }

}
