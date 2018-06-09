package data.room.voc;

import android.app.Application;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import data.room.WanicchouDatabase;
import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.models.DictionaryType;

/**
 * Repository to abstract the RPM of the Vocabulary Database
 */
public class VocabularyRepository {
    private VocabularyDao mVocabDao;
//    private LiveData< List<VocabularyEntity> > mAllVocab;
    private static final int ACTION_DELETE = 0;
    private static final int ACTION_UPDATE = 1;
    private static final int ACTION_INSERT = 2;

    /**
     * Constructs the repository
     * @param application The application the database exists in.
     */
    public VocabularyRepository(Application application){
        WanicchouDatabase database = WanicchouDatabase.getDatabase(application);

        mVocabDao = database.vocabularyDao();
//        mAllVocab = mVocabDao.getAllSavedWords();
    }


//    public LiveData<List<VocabularyEntity>> getAllWords() {
//        return mAllVocab;
//    }

    //TODO: If I can keeping the JapaneseVocabulary object,
    // pass that in and let this construct the vocab entity.
    /**
     * Inserts The vocabulary word into the database.
     * @param vocab The vocab to insert into the database.
     */
    public void insert(VocabularyEntity vocab){
        new entryModificationAsyncTask(mVocabDao, ACTION_INSERT).execute(vocab);
    }

    /**
     * Updates the vocabulary word in the database, if it exists
     * @param vocab The updated vocabulary entry.
     */
    public void update(VocabularyEntity vocab){
        new entryModificationAsyncTask(mVocabDao, ACTION_UPDATE).execute(vocab);
    }

    /**
     * Deletes the vocab from the database, if it exists.
     * @param vocab The vocab entry to delete from the database.
     */
    public void delete(VocabularyEntity vocab){
        new entryModificationAsyncTask(mVocabDao, ACTION_DELETE).execute(vocab);
    }

    /**
     * Queries for the word in the given dictionary type.
     * @param word The word to search for.
     * @param dictionaryType The dictionary type of the word-definition pair.
     * @return The word in the database if it exists, else null.
     */
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

    /**
     * Async task to insert/updateNote/delete entries in the database.
     */
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

    /**
     * Async task for queries into the database.
     */
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
