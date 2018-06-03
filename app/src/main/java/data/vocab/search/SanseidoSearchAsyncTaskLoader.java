package data.vocab.search;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.widget.Toast;

import com.waifusims.wanicchou.R;

import java.io.IOException;

import data.vocab.DictionaryType;
import data.vocab.MatchType;

/**
 * Created by Limegrass on 5/9/2018.
 */

public class SanseidoSearchAsyncTaskLoader extends AsyncTaskLoader<SanseidoSearch>{
    private String mSearchWord;
    private Toast mToast;
    private DictionaryType mDictionaryType;
    private MatchType mMatchType;
    private boolean hasDisplayedToast;

    /**
     * Constructor to perform a search Asynchronously.
     * @param context The context for the loader.
     * @param searchWord The word to search for.
     * @param dictionaryType The dictionary to search in.
     * @param matchType The way the search performs a match.
     */
    public SanseidoSearchAsyncTaskLoader(final Context context,
                                         String searchWord,
                                         DictionaryType dictionaryType,
                                         MatchType matchType) {
        super(context);
        mSearchWord = searchWord;
        mDictionaryType = dictionaryType;
        mMatchType = matchType;
        hasDisplayedToast = false;
    }

    /**
     * Set the dictionary type of the search if it changes.
     * @param dictionaryType the dictionary type to change the search to.
     */
    public void changeDictionaryType(DictionaryType dictionaryType){
        mDictionaryType = dictionaryType;
    }

    public boolean hasDisplayedToast(){
        return hasDisplayedToast;
    }

    public void setHasDisplayedToast(boolean displayed){
        hasDisplayedToast = displayed;
    }


    /**
     * Initializes all fields and displays a toast on load start.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();


        final Context context = getContext();
        final String searchingText = context.getString(R.string.word_searching);
        final int searchingToastDuration = Toast.LENGTH_LONG;

        if(!hasDisplayedToast){
            mToast = Toast.makeText(context, searchingText, searchingToastDuration);
            mToast.show();
        }
        forceLoad();
    }

    /**
     * Performs a Sanseido Search.
     * @return The completed SanseidoSearch object.
     */
    @Override
    public SanseidoSearch loadInBackground() {

        if (TextUtils.isEmpty(mSearchWord)) {
            return null;
        }

        SanseidoSearch search = null;
        try{
            search = new SanseidoSearch(mSearchWord, mDictionaryType, mMatchType);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        mToast.cancel();
        return search;
    }
}
