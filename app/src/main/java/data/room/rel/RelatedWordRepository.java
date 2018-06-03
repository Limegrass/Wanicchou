package data.room.rel;

import android.app.Application;
import android.os.AsyncTask;
import android.sax.RootElement;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import data.room.WanicchouDatabase;
import data.room.voc.VocabularyEntity;
import data.vocab.DictionaryType;

/**
 * A repository class to abstract all the Room Persistence Library elements underneath.
 */
public class RelatedWordRepository {

    private RelatedWordDao mRelatedWordDao;

    private static final int ACTION_DELETE = 0;
    private static final int ACTION_UPDATE = 1;
    private static final int ACTION_INSERT = 2;

    /**
     * Constructor for the repository.
     * @param application The application of the database.
     */
    public RelatedWordRepository(Application application){
        WanicchouDatabase database = WanicchouDatabase.getDatabase(application);
        mRelatedWordDao = database.relatedWordDao();
    }


    /**
     * Inserts a word into the database
     * @param relatedWordEntity the related word entity to insert.
     */
    public void insert(RelatedWordEntity relatedWordEntity){
        new entryModificationAsyncTask(mRelatedWordDao, ACTION_INSERT).execute(relatedWordEntity);
    }

    /**
     * Updates a related word entry in the database
     * @param relatedWordEntity The related word entity to updateNote.
     */
    public void update(RelatedWordEntity relatedWordEntity){
        new entryModificationAsyncTask(mRelatedWordDao, ACTION_UPDATE).execute(relatedWordEntity);
    }

    /**
     * Deletes a related word entry from the database if it exists.
     * @param relatedWordEntity The related word entity to delete.
     */
    public void delete(RelatedWordEntity relatedWordEntity){
        new entryModificationAsyncTask(mRelatedWordDao, ACTION_DELETE).execute(relatedWordEntity);
    }

    /**
     * Generates a list of related words given a VocabularyEntity's id key.
     * Checks all dictionary types.
     * @param vocabularyEntity the entity which a foreign key will be extracted from.
     * @return A list of related words from the related words database for
     * the given vocabulary entity.
     */
    public List<RelatedWordEntity> getRelatedWordList(VocabularyEntity vocabularyEntity) {
        List<RelatedWordEntity> ret = new ArrayList<>();
        try {
            for(DictionaryType type : DictionaryType.values())
            {
                List<RelatedWordEntity> partial = new queryAsyncTask(mRelatedWordDao, type).
                        execute(vocabularyEntity).get();
                ret.addAll(partial);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Generates a list of related words given a VocabularyEntity's id key
     * and a given dictionary type.
     * @param vocabularyEntity the foreign key of the vocabulary entity.
     * @param dictionaryType the dictionary type of the search.
     * @return A list of related words from the related words database for
     * the given vocabulary entity.
     */
    public List<RelatedWordEntity> getRelatedWordList(VocabularyEntity vocabularyEntity
            , DictionaryType dictionaryType) {

        List<RelatedWordEntity> ret = new ArrayList<>();
        try {
            ret = new queryAsyncTask(mRelatedWordDao, dictionaryType).execute(vocabularyEntity).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * AsyncTask to perform the entry modifications in the database.
     */
    private static class entryModificationAsyncTask extends AsyncTask<RelatedWordEntity, Void, Void> {
        private RelatedWordDao mAsyncTaskDao;
        private int mAction;

        protected entryModificationAsyncTask(RelatedWordDao dao, int action){
            mAsyncTaskDao = dao;
            mAction = action;
        }

        @Override
        protected Void doInBackground(RelatedWordEntity... relatedWordEntities) {
            switch (mAction) {
                case ACTION_UPDATE:
                    mAsyncTaskDao.update(relatedWordEntities[0]);
                    break;
                case ACTION_INSERT:
                    mAsyncTaskDao.insert(relatedWordEntities[0]);
                    break;
                case ACTION_DELETE:
                    mAsyncTaskDao.delete(relatedWordEntities[0]);
                    break;
                default:
            }
            return null;
        }
    }

    /**
     * Async Task to perform query tasks to the database.
     */
    private static class queryAsyncTask extends AsyncTask<VocabularyEntity, Void, List<RelatedWordEntity>> {
        private RelatedWordDao mAsyncTaskDao;
        private DictionaryType mDictionaryType;

        protected queryAsyncTask(RelatedWordDao dao, DictionaryType dictionaryType){
            mAsyncTaskDao = dao;
            mDictionaryType = dictionaryType;
        }

        /**
         * Find the list of related words given a foreign key and the dictionary type the
         * Async Task was constructed with.
         * @param entities the entity which a foreign key will be extracted from.
         * @return A list of words related to that key.
         */
        protected List<RelatedWordEntity> doInBackground(VocabularyEntity... entities) {
            return mAsyncTaskDao.getRelatedWordsFromId(entities[0], mDictionaryType);
        }

    }
}
