package data.room.rel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import java.util.List;

import data.room.voc.VocabularyEntity;
import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;

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

    /**
     * Gets the related words for all dictionary types for a certain word
     * @param vocabularyEntity The entity from which a foreign key will be extracted.
     * @return A list of all words related to the base vocabulary word.
     */
    public List<RelatedWordEntity> getRelatedWordList(VocabularyEntity vocabularyEntity) {
        return mRepo.getRelatedWordList(vocabularyEntity);
    }

    /**
     * Gets the related words for a certain dictionary type for a certain word
     * @param vocabularyEntity The entity from which a foreign key will be extracted.
     * @param dictionaryType the dictionary type of the relation to search for.
     * @return A list of all words related to the base vocabulary word in a certain dictionary.
     */
    public List<RelatedWordEntity> getRelatedWordList(VocabularyEntity vocabularyEntity,
                                                      DictionaryType dictionaryType) {
        return mRepo.getRelatedWordList(vocabularyEntity, dictionaryType);
    }
}
