package com.waifusims.wanicchou;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.waifusims.wanicchou.widgets.WordListAdapter;

import java.util.List;
import data.vocab.WordListEntry;

/**
 * Separate activity to display the related words of a SanseidoSearchResult.
 * If a word is long pressed, it will be searched and brought back to the home activity.
 */
public class DatabaseActivity extends AppCompatActivity
        implements WordListAdapter.WordViewHolder.ListItemClickListener {

    //TODO: Long press to open menu, option to delete, search
    //TODO: Options menu to input all words in the related word list if it doesn't exist
    private List<WordListEntry> mWordList;
    private WordListAdapter mAdapter;
    private RecyclerView mListRecyclerView;
    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback;

    // TODO: Long press word selection
    // TODO: Anki import for all selected words

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_words);
        final ActionBar actionbar = this.getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intentThatStartedThis = getIntent();

//        RELATED WORDS GARBAGE SET ONCLICK LISTENER TO MOVE TO OTHER ACTIVITY
        mListRecyclerView = findViewById(R.id.rv_related_word_block);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListRecyclerView.setLayoutManager(layoutManager);
        mListRecyclerView.setHasFixedSize(true);

        Bundle extras = intentThatStartedThis.getExtras();
        mWordList = (List<WordListEntry>) extras.get(getString(R.string.related_word_key));

        WordListAdapter.WordViewHolder.ListItemClickListener listener = this;
        mAdapter = new WordListAdapter(mWordList, listener);
        mListRecyclerView.setAdapter(mAdapter);

        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.word_list_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_db_delete:
                        actionMode.finish();
                        mAdapter.clearSelection();
                        return true;
                    case R.id.action_db_import:
                        actionMode.finish();
                        mAdapter.clearSelection();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mAdapter.clearSelection();
                mActionMode = null;
            }
        };
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
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onItemClick(int position) {
        if (mActionMode != null){
            toggleSelection(position);
        }
    }

    @Override
    public void onItemLongClick(int position) {
        if (mActionMode == null){
            mActionMode = startSupportActionMode(mActionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position){

        mAdapter.toggleSelection(position);

        int count = mAdapter.getSelectedItemsCount();
        if (count == 0) {
            mActionMode.finish();
        }
        else {
            mActionMode.setTitle(getString(R.string.title_selected, count));
            mActionMode.invalidate();
        }

    }
}
