package data.room.notes;

import android.app.Application;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import data.room.WanicchouDatabase;

public class NoteRepository {
    private NoteDao mNoteDao;
    private static final int ACTION_DELETE = 0;
    private static final int ACTION_UPDATE = 1;
    private static final int ACTION_INSERT = 2;

    /**
     * Constructs the note repository
     * @param application The application the database exists in.
     */
    public NoteRepository(Application application){
        WanicchouDatabase database = WanicchouDatabase.getDatabase(application);
        mNoteDao = database.noteDao();
    }

    /**
     * Inserts a note into the database.
     * @param note The note to insert.
     */
    public void insert(NoteEntity note){
        new entryModificationAsyncTask(mNoteDao, ACTION_INSERT).execute(note);
    }

    /**
     * Updates a note in the database, if it exists.
     * @param note The note to updateNote.
     */
    public void update(NoteEntity note){
        new entryModificationAsyncTask(mNoteDao, ACTION_UPDATE).execute(note);
    }

    /**
     * Deletes a note in the database, if it exists.
     * @param note The note to delete.
     */
    public void delete(NoteEntity note){
        new entryModificationAsyncTask(mNoteDao, ACTION_DELETE).execute(note);
    }

    /**
     * Queries the database for the notes related to a certain word.
     * @param word The word to search for.
     * @return The notes saved for the word.
     */
    public String getNoteOf(String word){
        String ret = null;

        try {
            ret = new queryAsyncTask(mNoteDao).execute(word).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Gets the note entity of a word asynchronously.
     * @param word The word whose notes to search for.
     * @return The NoteEntity of the word searched for if it exists, else null.
     */
    public NoteEntity getNoteEntityOf(String word){
        NoteEntity ret = null;

        try {
            ret = new noteEntityQueryTask(mNoteDao).execute(word).get();
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
    private static class entryModificationAsyncTask extends AsyncTask<NoteEntity, Void, Void> {
        private NoteDao mAsyncTaskDao;
        private int mAction;

        protected entryModificationAsyncTask(NoteDao dao, int action){
            mAsyncTaskDao = dao;
            mAction = action;
        }

        @Override
        protected Void doInBackground(NoteEntity... noteEntities) {
            switch (mAction) {
                case ACTION_UPDATE:
                    mAsyncTaskDao.update(noteEntities[0]);
                    break;
                case ACTION_INSERT:
                    mAsyncTaskDao.insert(noteEntities[0]);
                    break;
                case ACTION_DELETE:
                    mAsyncTaskDao.delete(noteEntities[0]);
                    break;
                default:
            }
            return null;
        }
    }

    /**
     * Async task for queries into the database.
     */
    private static class queryAsyncTask extends AsyncTask<String, Void, String>{
        private NoteDao mAsyncTaskDao;

        protected queryAsyncTask(NoteDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected String doInBackground(String... words) {
            return mAsyncTaskDao.getNoteOf(words[0]);
        }

    }

    /**
     * Async task to request NoteEntity.
     */
    private static class noteEntityQueryTask extends AsyncTask<String, Void, NoteEntity> {
        private NoteDao mAsyncTaskDao;

        protected noteEntityQueryTask(NoteDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected NoteEntity doInBackground(String... words) {
            return mAsyncTaskDao.getNoteEntityOf(words[0]);
        }

    }
}
