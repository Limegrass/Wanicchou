package com.waifusims.j_jlearnersdictionary;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.waifusims.j_jlearnersdictionary.databinding.ActivityRelatedWordsBinding;

import data.SanseidoSearch;

public class RelatedWordsActivity extends AppCompatActivity
        implements WordAdapter.ListItemClickListener{

    private SanseidoSearch searchData;
    private ActivityRelatedWordsBinding mBinding;
    private Toast mToast;

    private WordAdapter mAdapter;
    private RecyclerView mWordList;

    // TODO: Long press word selection
    // TODO: Anki import for all selected words

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_words);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_related_words);

        Intent intentThatStartedThis = getIntent();

//        RELATED WORDS GARBAGE SET ONCLICK LISTENER TO MOVE TO OTHER ACTIVITY
        mWordList = findViewById(R.id.rv_related_word_block);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mWordList.setLayoutManager(layoutManager);
        mWordList.setHasFixedSize(true);

        searchData = (SanseidoSearch) intentThatStartedThis
                .getExtras().get(getString(R.string.key_related_words));

        mAdapter = new WordAdapter(searchData.getRelatedWords(),
                this);
        mWordList.setAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mToast != null){
            mToast.cancel();
        }

        Context context = this;
        String message = "Item #: " + clickedItemIndex;
        int duration = Toast.LENGTH_SHORT;

        mToast = Toast.makeText(context, message, duration);
    }

}
