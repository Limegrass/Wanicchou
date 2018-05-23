package data.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

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

    public void delete(VocabularyEntity vocab){
        new entryModificationAsyncTask(mVocabDao, ACTION_DELETE).execute(vocab);
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



}
