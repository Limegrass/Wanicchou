package data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.Toast;

import com.waifusims.j_jlearnersdictionary.R;

import java.io.IOException;

/**
 * Created by Limegrass on 5/9/2018.
 */

public class SanseidoSearchAsyncTaskLoader extends AsyncTaskLoader<SanseidoSearch>{
    private String searchWord;
    private Toast mToast;

    public SanseidoSearchAsyncTaskLoader(final Context context, String searchWord){
        super(context);
        this.searchWord = searchWord;
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

        if (searchWord == null || searchWord.equals("")) {
            return null;
        }

        SanseidoSearch search = null;
        try{
            search = new SanseidoSearch(searchWord);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        mToast.cancel();
        return search;
    }
}
