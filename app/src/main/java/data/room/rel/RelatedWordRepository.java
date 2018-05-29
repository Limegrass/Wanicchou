package data.room.rel;

import android.app.Application;
import android.os.AsyncTask;
import android.sax.RootElement;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import data.room.WanicchouDatabase;
import data.vocab.DictionaryType;

public class RelatedWordRepository {

    private RelatedWordDao mRelatedWordDao;

    private static final int ACTION_DELETE = 0;
    private static final int ACTION_UPDATE = 1;
    private static final int ACTION_INSERT = 2;

    public RelatedWordRepository(Application application){
        WanicchouDatabase database = WanicchouDatabase.getDatabase(application);
        mRelatedWordDao = database.relatedWordDao();
    }


    public void insert(RelatedWordEntity relatedWordEntity){
        new entryModificationAsyncTask(mRelatedWordDao, ACTION_INSERT).execute(relatedWordEntity);
    }

    public void update(RelatedWordEntity relatedWordEntity){
        new entryModificationAsyncTask(mRelatedWordDao, ACTION_UPDATE).execute(relatedWordEntity);
    }

    public void delete(RelatedWordEntity relatedWordEntity){
        new entryModificationAsyncTask(mRelatedWordDao, ACTION_DELETE).execute(relatedWordEntity);
    }
    // TODO: UPDATE BUT IDK HOW TI WORKS BECAUSE GARBO DOCS

    //TODO: FUCKING GOOGLE CAN YOU JUST TELL ME WHAT THE RETURN TYPE OF A FAILED QUERY IS????
    public List<RelatedWordEntity> getRelatedWordList(int fkWordId) {
        List<RelatedWordEntity> ret = new ArrayList<>();
        try {
            for(DictionaryType type : DictionaryType.values())
            {
                List<RelatedWordEntity> partial = new queryAsyncTask(mRelatedWordDao, type).execute(fkWordId).get();
                ret.addAll(partial);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public List<RelatedWordEntity> getRelatedWordList(int fkWordId, DictionaryType dictionaryType) {
        List<RelatedWordEntity> ret = new ArrayList<>();
        try {
            for(DictionaryType type : DictionaryType.values())
            {
                List<RelatedWordEntity> partial = new queryAsyncTask(mRelatedWordDao, type).execute(fkWordId).get();
                ret.addAll(partial);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static class entryModificationAsyncTask extends AsyncTask<RelatedWordEntity, Void, Void> {
        private RelatedWordDao mAsyncTaskDao;
        private int mAction;

        protected entryModificationAsyncTask(RelatedWordDao dao, int action){
            mAsyncTaskDao = dao;
            mAction = action;
        }

        @Override
        protected Void doInBackground(RelatedWordEntity... vocabularyEntities) {
            switch (mAction) {
                case ACTION_UPDATE:
                    mAsyncTaskDao.update(vocabularyEntities[0]);
                    break;
                case ACTION_INSERT:
                    mAsyncTaskDao.insert(vocabularyEntities[0]);
                    break;
                case ACTION_DELETE:
                    mAsyncTaskDao.delete(vocabularyEntities[0]);
                    break;
                default:
            }
            return null;
        }
    }

    private static class queryAsyncTask extends AsyncTask<Integer, Void, List<RelatedWordEntity>> {
        private RelatedWordDao mAsyncTaskDao;
        private DictionaryType mDictionaryType;

        protected queryAsyncTask(RelatedWordDao dao, DictionaryType dictionaryType){
            mAsyncTaskDao = dao;
            mDictionaryType = dictionaryType;
        }

        protected List<RelatedWordEntity> doInBackground(Integer... keys) {
            return mAsyncTaskDao.getRelatedWordsFromId(keys[0], mDictionaryType);
        }

    }
}
