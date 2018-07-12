package data.room.context;

import android.app.Application;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import data.room.WanicchouDatabase;

public class ContextRepository {

    private ContextDao mContextDao;
    private static final int ACTION_DELETE = 0;
    private static final int ACTION_UPDATE = 1;
    private static final int ACTION_INSERT = 2;

    /**
     * Constructs the note repository
     * @param application The application the database exists in.
     */
    public ContextRepository(Application application){
        WanicchouDatabase database = WanicchouDatabase.getDatabase(application);
        mContextDao = database.contextDao();
    }

    /**
     * Inserts a note into the database.
     * @param note The note to insert.
     */
    public void insert(ContextEntity note){
        new entryModificationAsyncTask(mContextDao, ACTION_INSERT).execute(note);
    }

    /**
     * Updates a note in the database, if it exists.
     * @param note The note to updateNote.
     */
    public void update(ContextEntity note){
        new entryModificationAsyncTask(mContextDao, ACTION_UPDATE).execute(note);
    }

    /**
     * Deletes a note in the database, if it exists.
     * @param note The note to delete.
     */
    public void delete(ContextEntity note){
        new entryModificationAsyncTask(mContextDao, ACTION_DELETE).execute(note);
    }

    public void deleteAll(){
        new deleteAllTask().execute(mContextDao);
    }
    protected static class deleteAllTask extends AsyncTask<ContextDao, Void, Void>{
        @Override
        protected Void doInBackground(ContextDao... daos) {
            daos[0].deleteAll();
            return null;
        }
    }

    /**
     * Queries the database for the notes related to a certain word.
     * @param word The word to search for.
     * @return The notes saved for the word.
     */
    public String getContextOf(String word){
        String ret = null;

        try {
            ret = new contextQueryTask(mContextDao).execute(word).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Gets the context entity of a word asynchronously.
     * @param word The word whose context to search for.
     * @return The ContextEntity of the word searched for if it exists, else null.
     */
    public ContextEntity getContextEntityOf(String word){
        ContextEntity ret = null;

        try {
            ret = new contextEntityQueryTask(mContextDao).execute(word).get();
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
    private static class entryModificationAsyncTask extends AsyncTask<ContextEntity, Void, Void> {
        private ContextDao mAsyncTaskDao;
        private int mAction;

        protected entryModificationAsyncTask(ContextDao dao, int action){
            mAsyncTaskDao = dao;
            mAction = action;
        }

        @Override
        protected Void doInBackground(ContextEntity... contextEntities) {
            switch (mAction) {
                case ACTION_UPDATE:
                    mAsyncTaskDao.update(contextEntities[0]);
                    break;
                case ACTION_INSERT:
                    mAsyncTaskDao.insert(contextEntities[0]);
                    break;
                case ACTION_DELETE:
                    mAsyncTaskDao.delete(contextEntities[0]);
                    break;
                default:
            }
            return null;
        }
    }

    /**
     * Async task to request for the word context
     */
    private static class contextQueryTask extends AsyncTask<String, Void, String> {
        private ContextDao mAsyncTaskDao;

        protected contextQueryTask(ContextDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected String doInBackground(String... words) {
            return mAsyncTaskDao.getContextOf(words[0]);
        }

    }

    /**
     * Async task to request ContextEntities.
     */
    private static class contextEntityQueryTask extends AsyncTask<String, Void, ContextEntity> {
        private ContextDao mAsyncTaskDao;

        protected contextEntityQueryTask(ContextDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected ContextEntity doInBackground(String... words) {
            return mAsyncTaskDao.getContextEntityOf(words[0]);
        }

    }
}
