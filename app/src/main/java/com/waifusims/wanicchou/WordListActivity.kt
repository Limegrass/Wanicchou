//package com.waifusims.wanicchou
//
//import android.content.Intent
//import android.support.v4.app.NavUtils
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.view.MenuItem
//
//import com.waifusims.wanicchou.widgets.WordListAdapter
//import data.vocab.shared.WordListEntry
//
///**
// * Separate activity to display the related words of a SanseidoSearch.
// * If a word is long pressed, it will be searched and brought back to the home activity.
// */
//class WordListActivity : AppCompatActivity(), WordListAdapter.WordViewHolder.ListItemClickListener {
//
//    //TODO: Long press to open menu, option to delete, search
//    //TODO: Options menu to input all words in the related word list if it doesn't exist
//    private var wordList: List<WordListEntry>? = null
//    private var mAdapter: WordListAdapter<*>? = null
//    private var mWordList: RecyclerView? = null
//
//    // TODO: Long press word selection
//    // TODO: Anki import for all selected words
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_related_words)
//        val actionbar = this.actionBar
//        actionbar?.setDisplayHomeAsUpEnabled(true)
//
//        val intentThatStartedThis = intent
//
//        //        RELATED WORDS GARBAGE SET ONCLICK LISTENER TO MOVE TO OTHER ACTIVITY
//        mWordList = findViewById(R.id.rv_related_word_block)
//        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        mWordList!!.layoutManager = layoutManager
//        mWordList!!.setHasFixedSize(true)
//
//        val extras = intentThatStartedThis.extras
//        wordList = extras!!.get(getString(R.string.related_word_key)) as List<WordListEntry>
//
//        mAdapter = WordListAdapter(wordList,
//                this)
//        mWordList!!.adapter = mAdapter
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val itemId = item.itemId
//        if (itemId == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(this)
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onItemLongClick(clickedItemIndex: Int) {
//        val data = Intent()
//        data.putExtra(getString(R.string.desired_word_index_key), clickedItemIndex)
//        setResult(Activity.RESULT_OK, data)
//        finish()
//    }
//
//    override fun onItemClick(position: Int) {
//
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        setResult(Activity.RESULT_CANCELED)
//        finish()
//    }
//}
