package com.waifusims.wanicchou;

import android.app.ActionBar;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.waifusims.wanicchou.databinding.ActivityRelatedWordsBinding;

import data.vocab.jp.search.sanseido.SanseidoSearch;

/**
 * Separate activity to display the related words of a SanseidoSearch.
 * If a word is long pressed, it will be searched and brought back to the home activity.
 */
public class WordListActivity extends AppCompatActivity
        implements WordAdapter.ListItemClickListener{

    private SanseidoSearch searchData;
    private ActivityRelatedWordsBinding mBinding;

    private WordAdapter mAdapter;
    private RecyclerView mWordList;

    // TODO: Long press word selection
    // TODO: Anki import for all selected words

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_words);
        ActionBar actionbar = this.getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_related_words);

        Intent intentThatStartedThis = getIntent();

//        RELATED WORDS GARBAGE SET ONCLICK LISTENER TO MOVE TO OTHER ACTIVITY
        mWordList = findViewById(R.id.rv_related_word_block);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mWordList.setLayoutManager(layoutManager);
        mWordList.setHasFixedSize(true);

        Bundle extras = intentThatStartedThis.getExtras();
        searchData = (SanseidoSearch)extras.get(getString(R.string.related_word_key));

        mAdapter = new WordAdapter(searchData.getRelatedWords(),
                this);
        mWordList.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Intent data = new Intent();
        data.putExtra(getString(R.string.desired_word_index_key), clickedItemIndex);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}
