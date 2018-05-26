package data.room.voc;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import data.room.WanicchouDatabase;
import data.vocab.DictionaryType;

public class VocabularyRepository {
    private VocabularyDao mVocabDao;
    private LiveData< List<VocabularyEntity> > mAllVocab;
    private static final int ACTION_DELETE = 0;
    private static final int ACTION_UPDATE = 1;
    private static final int ACTION_INSERT = 2;

    public VocabularyRepository(Application application){
        WanicchouDatabase database = WanicchouDatabase.getDatabase(application);

        mVocabDao = database.vocabularyDao();
        mAllVocab = mVocabDao.getAllSavedWords();
    }


    public LiveData<List<VocabularyEntity>> getAllWords() {
        return mAllVocab;
    }

    public void insert(VocabularyEntity vocab){
        new entryModificationAsyncTask(mVocabDao, ACTION_INSERT).execute(vocab);
    }

    public void update(VocabularyEntity vocab){
        new entryModificationAsyncTask(mVocabDao, ACTION_UPDATE).execute(vocab);
    }

    public void delete(VocabularyEntity vocab){
        new entryModificationAsyncTask(mVocabDao, ACTION_DELETE).execute(vocab);
    }
    // TODO: UPDATE BUT IDK HOW TI WORKS BECAUSE GARBO DOCS

    //TODO: FUCKING GOOGLE CAN YOU JUST TELL ME WHAT THE RETURN TYPE OF A FAILED QUERY IS????
    public VocabularyEntity getWord(String word, DictionaryType dictionaryType){
        VocabularyEntity ret = null;
        try {
            ret = new queryAsyncTask(mVocabDao, dictionaryType).execute(word).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static class entryModificationAsyncTask extends AsyncTask<VocabularyEntity, Void, Void>{
        private VocabularyDao mAsyncTaskDao;
        private int mAction;

        protected entryModificationAsyncTask(VocabularyDao dao, int action){
            mAsyncTaskDao = dao;
            mAction = action;
        }

        @Override
        protected Void doInBackground(VocabularyEntity... vocabularyEntities) {
            switch (mAction) {
                case ACTION_UPDATE:
                    mAsyncTaskDao.updateWord(vocabularyEntities[0]);
                    break;
                case ACTION_INSERT:
                    mAsyncTaskDao.insertWord(vocabularyEntities[0]);
                    break;
                case ACTION_DELETE:
                    mAsyncTaskDao.deleteWord(vocabularyEntities[0]);
                    break;
                default:
            }
            return null;
        }
    }

    private static class queryAsyncTask extends AsyncTask<String, Void, VocabularyEntity>{
        private VocabularyDao mAsyncTaskDao;
        private DictionaryType mDictionaryType;

        protected queryAsyncTask(VocabularyDao dao, DictionaryType dictionaryType){
            mAsyncTaskDao = dao;
            mDictionaryType = dictionaryType;
        }

        @Override
        protected VocabularyEntity doInBackground(String... words) {
            return mAsyncTaskDao.getWord(words[0], mDictionaryType.toString());
        }

    }

}
