package data.vocab.search;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.widget.Toast;

import com.waifusims.j_jlearnersdictionary.R;

import java.io.IOException;

import data.vocab.DictionaryType;

/**
 * Created by Limegrass on 5/9/2018.
 */

public class SanseidoSearchAsyncTaskLoader extends AsyncTaskLoader<SanseidoSearch>{
    private String mSearchWord;
    private Toast mToast;
    private DictionaryType mDictionaryType;

    public SanseidoSearchAsyncTaskLoader(final Context context, String searchWord, DictionaryType dictionaryType){
        super(context);
        mSearchWord = searchWord;
        mDictionaryType = dictionaryType;
    }

    public void changeDictionaryType(DictionaryType dictionaryType){
        mDictionaryType = dictionaryType;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();


        final Context context = getContext();
        final String searchingText = context.getString(R.string.word_searching);
        final int searchingToastDuration = Toast.LENGTH_LONG;

        mToast = Toast.makeText(context, searchingText, searchingToastDuration);
        mToast.show();
        forceLoad();
    }

    @Override
    public SanseidoSearch loadInBackground() {

        if (TextUtils.isEmpty(mSearchWord)) {
            return null;
        }

        SanseidoSearch search = null;
        try{
            Context context = getContext();
            search = new SanseidoSearch(mSearchWord, mDictionaryType);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        mToast.cancel();
        return search;
    }
}
