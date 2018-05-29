package data.room.rel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import java.util.List;

import data.vocab.DictionaryType;

public class RelatedWordViewModel extends AndroidViewModel{
    private RelatedWordRepository mRepo;

    public RelatedWordViewModel(Application application){
        super(application);
        mRepo = new RelatedWordRepository(application);
    }

    public void update(RelatedWordEntity relatedWordEntity){
        mRepo.update(relatedWordEntity);
    }

    public void insert(RelatedWordEntity relatedWordEntity){
        mRepo.insert(relatedWordEntity);
    }

    public void delete(RelatedWordEntity relatedWordEntity){
        mRepo.delete(relatedWordEntity);
    }

    public List<RelatedWordEntity> getRelatedWordList(int fkWordId) {
        return mRepo.getRelatedWordList(fkWordId);
    }

    public List<RelatedWordEntity> getRelatedWordList(int fkWordId, DictionaryType dictionaryType) {
        return mRepo.getRelatedWordList(fkWordId, dictionaryType);
    }
}
