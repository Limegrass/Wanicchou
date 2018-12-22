//package com.waifusims.wanicchou
//
//import android.support.v4.app.NavUtils
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.support.v7.view.ActionMode
//import android.view.Menu
//import android.view.MenuItem
//
//import com.waifusims.wanicchou.widgets.WordListAdapter
//import data.vocab.shared.WordListEntry
//
///**
// * Separate activity to display the related words of a SanseidoSearch.
// * If a word is long pressed, it will be searched and brought back to the home activity.
// */
//class DatabaseActivity : AppCompatActivity(), WordListAdapter.WordViewHolder.ListItemClickListener {
////
////    //TODO: Long press to open menu, option to delete, search
////    //TODO: Options menu to input all words in the related word list if it doesn't exist
////    private var mWordList: List<WordListEntry>? = null
////    private var mAdapter: WordListAdapter<*>? = null
////    private var mListRecyclerView: RecyclerView? = null
////    private var mActionMode: ActionMode? = null
////    private var mActionModeCallback: ActionMode.Callback? = null
////
////    // TODO: Long press word selection
////    // TODO: Anki import for all selected words
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_related_words)
////        val actionbar = this.actionBar
////        actionbar?.setDisplayHomeAsUpEnabled(true)
////
////        val intentThatStartedThis = intent
////
////        //        RELATED WORDS GARBAGE SET ONCLICK LISTENER TO MOVE TO OTHER ACTIVITY
////        mListRecyclerView = findViewById(R.id.rv_related_word_block)
////        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
////        mListRecyclerView!!.layoutManager = layoutManager
////        mListRecyclerView!!.setHasFixedSize(true)
////
////        val extras = intentThatStartedThis.extras
////        mWordList = extras!!.get(getString(R.string.related_word_key)) as List<WordListEntry>
////
////
////        val listener = this
////        mAdapter = WordListAdapter(mWordList, listener)
////        mListRecyclerView!!.adapter = mAdapter
////
////        mActionModeCallback = object : ActionMode.Callback {
////
////            override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
////                actionMode.menuInflater.inflate(R.menu.word_list_menu, menu)
////                return true
////            }
////
////            override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
////                return false
////            }
////
////            override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
////                when (menuItem.itemId) {
////                    R.id.action_db_delete -> {
////                        actionMode.finish()
////                        mAdapter!!.clearSelection()
////                        return true
////                    }
////                    R.id.action_db_import -> {
////                        actionMode.finish()
////                        mAdapter!!.clearSelection()
////                        return true
////                    }
////                    else -> return false
////                }
////            }
////
////            override fun onDestroyActionMode(actionMode: ActionMode) {
////                mAdapter!!.clearSelection()
////                mActionMode = null
////            }
////        }
////    }
////
////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        val itemId = item.itemId
////        if (itemId == android.R.id.home) {
////            NavUtils.navigateUpFromSameTask(this)
////        }
////        return super.onOptionsItemSelected(item)
////    }
////
////    override fun onBackPressed() {
////        super.onBackPressed()
////        setResult(Activity.RESULT_CANCELED)
////        finish()
////    }
////
////    override fun onItemClick(position: Int) {
////        if (mActionMode != null) {
////            toggleSelection(position)
////        }
////    }
////
////    override fun onItemLongClick(position: Int) {
////        if (mActionMode == null) {
////            mActionMode = startSupportActionMode(mActionModeCallback!!)
////        }
////        toggleSelection(position)
////    }
////
////    private fun toggleSelection(position: Int) {
////
////        mAdapter!!.toggleSelection(position)
////
////        val count = mAdapter!!.selectedItemsCount
////        if (count == 0) {
////            mActionMode!!.finish()
////        } else {
////            mActionMode!!.title = getString(R.string.title_selected, count)
////            mActionMode!!.invalidate()
////        }
////
////    }
//}
