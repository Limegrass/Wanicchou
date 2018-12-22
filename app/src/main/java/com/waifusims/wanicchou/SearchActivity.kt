package com.waifusims.wanicchou

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast

import com.waifusims.wanicchou.databinding.ActivitySearchBinding

import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

import data.room.WanicchouDatabase
import data.room.entity.Vocabulary
import data.vocab.model.lang.JapaneseVocabulary
import data.core.OnJavaScriptCompleted
import data.room.viewmodel.SearchViewModel
import data.vocab.search.SearchProvider
import data.vocab.shared.WordListEntry
import util.anki.AnkiDroidHelper

// TODO: Automatically select EJ for English input
//TODO:  Horizontal UI
// TODO: Toasts for DB searches
//TODO : Add click listener for Def label
class SearchActivity : AppCompatActivity() {
    //TODO: Search Suggestions
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.search_menu, menu)
//        return true
//    }
//
//    private var mBinding: ActivitySearchBinding? = null
//    private var mAnkiDroid: AnkiDroidHelper? = null
//    private var mToast: Toast? = null
//    private var mSearchViewModel : SearchViewModel = null
//    private var sharedPreferencesHelper: WanicchouSharedPreferencesHelper? = null
//
//    // Results screen and Search screen could be separate?
//    private val searchWord: String
//        get() = mBinding!!.searchBox.etSearchBox.text.toString()
//
//    //TODO: REfactor this to use a proper view model
//    private val databaseActivityIntent: Intent
//        get() {
//            val databaseActivityIntent = Intent(applicationContext, DatabaseActivity::class.java)
//
//            val dbWords = mVocabViewModel!!.getAllSavedWords()
//
//            databaseActivityIntent
//                    .putParcelableArrayListExtra(getString(R.string.related_word_key),
//                            dbWords as ArrayList<out Parcelable>)
//
//            return databaseActivityIntent
//        }
//
//    /* ==================================== +Lifecycle ================================== */
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
//        val context = this@SearchActivity
//        sharedPreferencesHelper = WanicchouSharedPreferencesHelper(context)
////        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
//        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
//        setUpUI()
//    }
//
//    //TODO: This entire thing once UI is made
//    private fun setUpUI() {
//        setUpClickListeners()
//        setUpOnFocusChangeListeners()
//        setUpKeyListeners()
//        mBinding!!.swipeRefresh.setOnRefreshListener {
//            val word = searchWord
//            if (!TextUtils.isEmpty(word)) {
//                searchOnline(word)
//            } else {
//                mBinding!!.swipeRefresh.isRefreshing = false
//            }
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
////        outState.putParcelable(getString(R.string.last_searched_key), mLastSearched)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
////        mLastSearched = savedInstanceState.getParcelable(getString(R.string.last_searched_key))
//        showWordOnUI()
//        showAnkiRelatedUIElements()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (mLastSearched != null) {
//            // Save the word so it can be retrieved and researched when the activity is returned.
//            sharedPreferencesHelper!!
//                    .putString(R.string.search_word_key,
//                            mLastSearched!!.dictionaryEntry.word)
//            sharedPreferencesHelper!!
//                    .putString(R.string.dic_type_key,
//                            mLastSearched!!.dictionaryEntry.dictionaryType.toString())
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        //TODO: Move this away from onResume to reduce startup time
//        if (mWebPage == null) {
//            val lastSearchedWord = sharedPreferencesHelper!!.getString(R.string.search_word_key)
//            val dicType = sharedPreferencesHelper!!.getString(R.string.dic_type_key)
//            val dictionaryType = DictionaryTypes.getDictionaryType(dicType)
//            if (!TextUtils.isEmpty(lastSearchedWord)) {
//                showWordFromDB(lastSearchedWord, dictionaryType)
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        if (sharedPreferencesHelper!!.autoDeleteOption() == "close") {
//            clearDBAsyncTask().execute(this@SearchActivity)
//        }
//        super.onDestroy()
//    }
//
//    protected class clearDBAsyncTask : AsyncTask<Context, Void, Void>() {
//        override fun doInBackground(vararg contexts: Context): Void? {
//            val database = WanicchouDatabase.getDatabase(contexts[0])
//            database.clearAllTables()
//            return null
//        }
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    val key = getString(R.string.desired_word_index_key)
//                    if (data!!.hasExtra(key)) {
//                        //TODO: Show readings on Related Words so it makes sense to see multiple words with same def
//                        val desiredRelatedWordIndex = data.extras!!.getInt(key)
//                        val desiredWord = mLastSearched!!.relatedWords[desiredRelatedWordIndex]
//
//                        mBinding!!.searchBox.etSearchBox.setText(desiredWord.relatedWord)
//                        if (mWebPage != null && !TextUtils.isEmpty(desiredWord.relatedWord)) {
//                            mWebPage!!.navigateRelatedWord(desiredWord,
//                                    sharedPreferencesHelper!!.matchType)
//                        } else if (!TextUtils.isEmpty(desiredWord.relatedWord)) {
//                            val word = desiredWord.relatedWord
//                            if (searchDatabase(word)) {
//                                val message = getString(R.string.searched_from_db_toast, word)
//                                val duration = Toast.LENGTH_SHORT
//                                showToast(message, duration)
//                            } else if (searchOnline(word)) {
//                            }
//                        }
//                    }
//                    val message = getString(R.string.searching_related_toast)
//                    val duration = Toast.LENGTH_SHORT
//                    showToast(message, duration)
//                }
//                else -> onResume()
//            }
//        }
//    }
//
//    /* ==================================== +Search ================================== */
//
//    override fun onJavaScriptCompleted() {
//        mLastSearched = mWebPage!!.search
//        runOnUiThread { handleSearchResult() }
//
//        val message: String
//        val duration = Toast.LENGTH_SHORT
//        if (!TextUtils.isEmpty(mLastSearched!!.dictionaryEntry.word)) {
//            message = getString(R.string.word_search_success, mLastSearched!!.dictionaryEntry.word)
//        } else {
//            message = getString(R.string.word_search_failure, mBinding!!.searchBox.etSearchBox.text.toString())
//        }
//        //        // TODO: Check for network and http request time outs
//        //        message = getString(R.string.word_search_failure);
//        showToast(message, duration)
//    }
//
//
//    /* ==================================== +Databases ================================== */
//
//    private fun getExistingRelatedWordsFromDb(word: String?): MutableList<WordListEntry>? {
//        val entity = mVocabViewModel!!.getWord(word,
//                sharedPreferencesHelper!!.dictionaryPreference) ?: return null
//
//        val relatedWords = ArrayList<WordListEntry>()
//        val provider = sharedPreferencesHelper!!.searchProvider ?: return null
//        for (dictionaryType in DictionaryTypes.getAllDictionaryTypeForLanguage(provider.Translation!!)!!) {
//            val relatedWordEntities = mRelatedWordViewModel!!.getRelatedWordList(entity, dictionaryType)
//            for (relatedWordEntity in relatedWordEntities) {
//                relatedWords.add(WordListEntry(relatedWordEntity.getRelatedWord(), relatedWordEntity.getDictionaryType()))
//            }
//        }
//        return relatedWords
//    }
//
//    private fun addWordsToRelatedWordsDb(vocabulary: Vocabulary, newRelatedWords: List<WordListEntry>) {
//        val entity = getWordFromDb(vocabulary.word, vocabulary.dictionaryType) ?: return
//        for (entry in newRelatedWords) {
//            val relatedWordToAdd = RelatedWordEntity(entity, entry.relatedWord,
//                    entry.dictionaryType!!.toString())
//            mRelatedWordViewModel!!.insert(relatedWordToAdd)
//        }
//    }
//
//    private fun getWordFromDb(word: String?, dictionaryType: DictionaryType?): Vocabulary? {
//        return mVocabViewModel!!.getWord(word, dictionaryType)
//    }
//
//
//    // TODO: Redo this method because it doesn't do anything, call it in onFocusChanged for tvDefinition
//    private fun updateDefinition(vocab: Vocabulary, definition: String) {
//        //I need the ID, so I have to DB query. Could work around if I save ID
//        val wordInDb = getWordFromDb(vocab.word, vocab.dictionaryType) ?: return
//        wordInDb.setDefinition(definition)
//        mVocabViewModel!!.update(wordInDb)
//    }
//
//    private fun showWordFromDB(searchWord: String?, dictionaryType: DictionaryType?): Boolean {
//        val vocabEntity = getWordFromDb(searchWord, dictionaryType)
//        //TODO: Give option of web search
//        if (vocabEntity != null) {
//            if (DictionaryTypes.getDictionaryType(vocabEntity.getDictionaryType()) !== sharedPreferencesHelper!!.dictionaryPreference) {
//                return false
//            }
//
//            val vocabulary = JapaneseVocabulary(vocabEntity)
//
//
//            val note = mNoteViewModel!!.getNoteOf(vocabEntity.word)
//            val wordContext = mContextViewModel!!.getContextOf(vocabEntity.word)
//
//            mBinding!!.ankiAdditionalFields.tvNotes.setText(note)
//            mBinding!!.ankiAdditionalFields.etNotes.setText(note)
//            mBinding!!.ankiAdditionalFields.tvContext.setText(wordContext)
//            mBinding!!.ankiAdditionalFields.etContext.setText(wordContext)
//
//            mLastSearched = SanseidoSearch(vocabulary,
//                    getExistingRelatedWordsFromDb(vocabulary.word))
//            showWordOnUI()
//            showAnkiRelatedUIElements()
//            return true
//        }
//        return false
//    }
//
//    private fun updateNote() {
//        val note = mNoteViewModel!!.getNoteOf(mLastSearched!!.dictionaryEntry.word)
//        if (note != null) {
//            mNoteViewModel!!.updateNote(mLastSearched!!.dictionaryEntry.word,
//                    mBinding!!.ankiAdditionalFields.tvNotes.text.toString())
//        } else {
//            val event = "info"
//            if (shouldSaveSearch(event)) {
//                saveSearch()
//            }
//        }
//    }
//
//    private fun saveSearch() {
//        if (mVocabViewModel!!.insert(mLastSearched!!.dictionaryEntry)) {
//            addWordsToRelatedWordsDb(mLastSearched!!.dictionaryEntry,
//                    mLastSearched!!.relatedWords)
//            mNoteViewModel!!.insertNewNote(mLastSearched!!.dictionaryEntry.word)
//            mContextViewModel!!.insertNewContext(mLastSearched!!.dictionaryEntry.word)
//        }
//    }
//
//    private fun shouldSaveSearch(event: String): Boolean {
//        val vocab = mLastSearched!!.dictionaryEntry
//        val wordInDb = getWordFromDb(vocab.word, vocab.dictionaryType)
//        if (wordInDb != null) {
//            return false
//        }
//
//        when (sharedPreferencesHelper!!.autoSaveOption()) {
//            //Catches failure event only
//            "all" -> return true
//            "success" -> {
//                if (event == "success") {
//                    return true
//                }
//                return if (event == "info") {
//                    true
//                } else false
//            }
//            "info" -> {
//                return if (event == "info") {
//                    true
//                } else false
//            }
//            else -> return false
//        }
//    }
//
//    private fun updateContext() {
//        val wordContext = mContextViewModel!!.getContextOf(mLastSearched!!.dictionaryEntry.word)
//        if (wordContext != null) {
//            mContextViewModel!!.updateContext(mLastSearched!!.dictionaryEntry.word,
//                    mBinding!!.ankiAdditionalFields.tvContext.text.toString())
//        } else {
//            val event = "info"
//            if (shouldSaveSearch(event)) {
//                saveSearch()
//            }
//        }
//    }
//
//
//    /* ==================================== +UI and +Helpers ================================== */
//    /**
//     * Helper class to hide and show Anki import related UI elements after a search is performed.
//     */
//    private fun showAnkiRelatedUIElements() {
//        mBinding!!.btnRelatedWords.visibility = View.VISIBLE
//        mBinding!!.ankiAdditionalFields.viewAnkiFields.visibility = View.VISIBLE
//        if (AnkiDroidHelper.isApiAvailable(this)) {
//            mBinding!!.fab.visibility = View.VISIBLE
//        } else {
//            mBinding!!.fab.visibility = View.INVISIBLE
//        }
//    }
//
//    private fun clearAnkiFields() {
//        mBinding!!.ankiAdditionalFields.tvContext.text = ""
//        mBinding!!.ankiAdditionalFields.etContext.setText("")
//        mBinding!!.ankiAdditionalFields.tvNotes.text = ""
//        mBinding!!.ankiAdditionalFields.etNotes.setText("")
//    }
//
//    private fun setUpOnFocusChangeListeners() {
//        mBinding!!.vocabularyInformation.etDefinition.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
//            if (!hasFocus && mLastSearched != null) {
//                val changedText = mBinding!!.vocabularyInformation.etDefinition.text.toString()
//                mBinding!!.vocabularyInformation.tvDefinition.text = changedText
//                mLastSearched!!.dictionaryEntry.definition = changedText
//                updateDefinition(mLastSearched!!.dictionaryEntry, changedText)
//                mBinding!!.vocabularyInformation.vsDefinition.showNext()
//                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(
//                        mBinding!!.ankiAdditionalFields.etNotes.windowToken,
//                        0
//                )
//            }
//        }
//
//        mBinding!!.ankiAdditionalFields.etContext.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
//            if (!hasFocus) {
//                val changedText = mBinding!!.ankiAdditionalFields.etContext.text.toString()
//                mBinding!!.ankiAdditionalFields.tvContext.text = changedText
//                mBinding!!.ankiAdditionalFields.vsContext.showNext()
//                updateContext()
//                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(
//                        mBinding!!.ankiAdditionalFields.etNotes.windowToken,
//                        0
//                )
//            }
//        }
//
//        mBinding!!.ankiAdditionalFields.etNotes.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
//            if (!hasFocus) {
//                val changedText = mBinding!!.ankiAdditionalFields.etNotes.text.toString()
//                mBinding!!.ankiAdditionalFields.tvNotes.text = changedText
//                mBinding!!.ankiAdditionalFields.vsNotes.showNext()
//                updateNote()
//                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(
//                        mBinding!!.ankiAdditionalFields.etNotes.windowToken,
//                        0
//                )
//
//            }
//        }
//    }
//
//    private fun setUpClickListeners() {
//        // Definition TV/ET
//        mBinding!!.vocabularyInformation.tvDefinition.setOnClickListener {
//            mBinding!!.vocabularyInformation.vsDefinition.showNext()
//            mBinding!!.vocabularyInformation.etDefinition.requestFocus()
//            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.showSoftInput(mBinding!!.vocabularyInformation.etDefinition,
//                    InputMethodManager.SHOW_IMPLICIT)
//        }
//
//
//        // Context Label, TV, and ET
//        mBinding!!.ankiAdditionalFields.tvContextLabel.setOnClickListener {
//            mBinding!!.ankiAdditionalFields.vsContext.showNext()
//            mBinding!!.ankiAdditionalFields.etContext.requestFocus()
//            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.showSoftInput(mBinding!!.ankiAdditionalFields.etContext,
//                    InputMethodManager.SHOW_IMPLICIT)
//        }
//
//        mBinding!!.ankiAdditionalFields.tvContext.setOnClickListener {
//            mBinding!!.ankiAdditionalFields.vsContext.showNext()
//            mBinding!!.ankiAdditionalFields.etContext.requestFocus()
//            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.showSoftInput(mBinding!!.ankiAdditionalFields.etContext,
//                    InputMethodManager.SHOW_IMPLICIT)
//        }
//
//
//        // Notes label, TV, ET
//        mBinding!!.ankiAdditionalFields.tvNotesLabel.setOnClickListener {
//            mBinding!!.ankiAdditionalFields.vsNotes.showNext()
//            mBinding!!.ankiAdditionalFields.etNotes.requestFocus()
//            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.showSoftInput(mBinding!!.ankiAdditionalFields.etNotes,
//                    InputMethodManager.SHOW_IMPLICIT)
//        }
//        mBinding!!.ankiAdditionalFields.tvNotes.setOnClickListener {
//            mBinding!!.ankiAdditionalFields.vsNotes.showNext()
//            mBinding!!.ankiAdditionalFields.etNotes.requestFocus()
//            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.showSoftInput(mBinding!!.ankiAdditionalFields.etNotes,
//                    InputMethodManager.SHOW_IMPLICIT)
//        }
//
//
//
//        mBinding!!.btnRelatedWords.setOnClickListener {
//            val intentStartRelatedWordsActivity = Intent(applicationContext, WordListActivity::class.java)
//
//            intentStartRelatedWordsActivity
//                    .putParcelableArrayListExtra(getString(R.string.related_word_key),
//                            mLastSearched!!.relatedWords as ArrayList<out Parcelable>)
//
//            startActivityForResult(intentStartRelatedWordsActivity, SEARCH_ACTIVITY_REQUEST_CODE)
//        }
//
//        //TODO: Fix it so it requests on add card, not on start up. Avoid crashes before addition.
//
//        val fab = findViewById<FloatingActionButton>(R.id.fab)
//        // TODO: Hide if Anki is not installed.
//        fab.setOnClickListener(View.OnClickListener {
//            if (mAnkiDroid == null) {
//                mAnkiDroid = AnkiDroidHelper(this@SearchActivity)
//            }
//            if (mAnkiDroid!!.shouldRequestPermission()) {
//                mAnkiDroid!!.requestPermission(this@SearchActivity, ADD_PERM_REQUEST)
//                return@OnClickListener
//            }
//
//            val notes = mBinding!!.ankiAdditionalFields.etNotes.text.toString()
//            val wordContext = mBinding!!.ankiAdditionalFields.etContext.text.toString()
//            mAnkiDroid!!.addWordToAnki(mLastSearched, notes, wordContext)
//            val message = getString(R.string.anki_added_toast)
//            val duration = Toast.LENGTH_SHORT
//            showToast(message, duration)
//
//
//            val autoDeleteOption = sharedPreferencesHelper!!.autoDeleteOption()
//            if (autoDeleteOption == "import") {
//                mNoteViewModel!!.delete(mLastSearched!!.dictionaryEntry.word)
//                mContextViewModel!!.delete(mLastSearched!!.dictionaryEntry.word)
//                mRelatedWordViewModel!!.deleteWordsRelatedTo(mLastSearched!!.dictionaryEntry.word)
//                mVocabViewModel!!.delete(mLastSearched!!.dictionaryEntry.word,
//                        mLastSearched!!.dictionaryEntry.dictionaryType)
//            }
//        })
//    }
//
//    private fun searchDatabase(word: String?): Boolean {
//        var word = word
//        clearAnkiFields()
//        word = word!!.trim { it <= ' ' }
//        val provider = sharedPreferencesHelper!!.searchProvider
//
//        var dicPref: DictionaryType? = null
//        try {
//            val autoAssigner = provider!!.DICTIONARY_TYPE_CLASS!!.getMethod("assignTypeByInput", String::class.java!!)
//            dicPref = autoAssigner.invoke(null, word)
//        } catch (e: NoSuchMethodException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        } catch (e: InvocationTargetException) {
//            e.printStackTrace()
//        }
//
//        if (dicPref == null) {
//            dicPref = sharedPreferencesHelper!!.dictionaryPreference
//        }
//
//        return showWordFromDB(word, dicPref)
//    }
//
//    private fun searchOnline(word: String?): Boolean {
//        try {
//            val provider = sharedPreferencesHelper!!.searchProvider
//            var dicPref: DictionaryType? = null
//
//            val autoAssigner = provider!!.DICTIONARY_TYPE_CLASS!!.getMethod("assignTypeByInput", String::class.java!!)
//            dicPref = autoAssigner.invoke(null, word)
//            if (dicPref == null) {
//                dicPref = sharedPreferencesHelper!!.dictionaryPreference
//            }
//
//            val context = this@SearchActivity
//            val listener = this@SearchActivity
//
//            //TODO: Reuse existing webview if possible
//
//            val webViewContructor = SearchProvider.WEB_VIEW_CLASS!!.getConstructor(
//                    Context::class.java,
//                    String::class.java,
//                    DictionaryType::class.java,
//                    provider.MATCH_TYPE_CLASS,
//                    OnJavaScriptCompleted::class.java
//            )
//            mWebPage = webViewContructor.newInstance(
//                    context,
//                    word,
//                    dicPref,
//                    provider.MATCH_TYPE_CLASS!!.cast(sharedPreferencesHelper!!.matchType),
//                    listener
//            )
//            return true
//        } catch (e: NoSuchMethodException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        } catch (e: InvocationTargetException) {
//            e.printStackTrace()
//        } catch (e: InstantiationException) {
//            e.printStackTrace()
//        }
//
//        return false
//    }
//
//    private fun setUpKeyListeners() {
//        mBinding!!.searchBox.etSearchBox.setOnKeyListener(OnKeyListener { searchBox, keyCode, keyEvent ->
//            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
//                when (keyCode) {
//                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
//
//                        var message = getString(R.string.word_searching)
//                        val duration = Toast.LENGTH_SHORT
//                        showToast(message, duration)
//
//                        val word = searchWord
//                        if (searchDatabase(word)) {
//                            message = getString(R.string.searched_from_db_toast, word)
//                            showToast(message, duration)
//                        } else if (searchOnline(word)) {
//                            //Handled by handleSearchResult for now
//                        }
//                        return@OnKeyListener false
//                    }
//                    else -> return@OnKeyListener false
//                }
//            }
//            false
//        })
//    }
//
//    private fun handleSearchResult() {
//        val event: String
//        if (mLastSearched != null) {
//            if (!TextUtils.isEmpty(mLastSearched!!.dictionaryEntry.word)) {
//                event = "success"
//                if (shouldSaveSearch(event)) {
//                    saveSearch()
//                }
//                showWordOnUI()
//
//                showAnkiRelatedUIElements()
//            } else {
//                event = "failure"
//                if (shouldSaveSearch(event)) {
//                    addInvalidWord(mLastSearched!!)
//                }
//            }
//        }
//        mBinding!!.swipeRefresh.isRefreshing = false
//    }
//
//    private fun showWordOnUI() {
//        assert(mLastSearched != null)
//        val vocabulary = mLastSearched!!.dictionaryEntry
//        val definition = vocabulary.definition
//        mBinding!!.vocabularyInformation.tvWord.text = vocabulary.word
//        mBinding!!.vocabularyInformation.tvDefinition.text = definition
//        mBinding!!.vocabularyInformation.etDefinition.setText(definition)
//        mBinding!!.vocabularyInformation.tvReading.text = vocabulary.reading
//        if (!TextUtils.isEmpty(vocabulary.pitch)) {
//            mBinding!!.vocabularyInformation.tvPitch.text = vocabulary.pitch
//            mBinding!!.vocabularyInformation.tvPitch.visibility = View.VISIBLE
//        } else {
//            mBinding!!.vocabularyInformation.tvPitch.visibility = View.INVISIBLE
//        }
//
//        mBinding!!.vocabularyInformation.tvDictionary.text = vocabulary.dictionaryType.toDisplayText()
//
//        val note = mNoteViewModel!!.getNoteOf(mLastSearched!!.dictionaryEntry.word)
//        mBinding!!.ankiAdditionalFields.etNotes.setText(note)
//        mBinding!!.ankiAdditionalFields.tvNotes.setText(note)
//
//        val wordContext = mContextViewModel!!.getContextOf(mLastSearched!!.dictionaryEntry.word)
//        mBinding!!.ankiAdditionalFields.etContext.setText(wordContext)
//        mBinding!!.ankiAdditionalFields.tvContext.setText(wordContext)
//
//
//    }
//
//    private fun addInvalidWord(search: Search) {
//        // TODO: Add empty entry to db for failed searches that aren't network errors.
//        // TODO: Probably a null somewhere since I do this
//        val dictionaryType = search.dictionaryEntry.dictionaryType
//        val invalidWord = JapaneseVocabulary(mBinding!!.searchBox.etSearchBox
//                .text.toString(), dictionaryType)
//
//        val entity = getWordFromDb(search.dictionaryEntry.word, dictionaryType)
//        if (entity == null) {
//            mVocabViewModel!!.insert(invalidWord)
//        }
//        // TODO: Maybe a different error message
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val menuInflater = menuInflater
//        menuInflater.inflate(R.menu.search_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val itemId = item.itemId
//
//        when (itemId) {
//            R.id.action_settings -> {
//                val context = this
//                val childActivity = SettingsActivity::class.java
//                val startSettingsActivityIntent = Intent(context, childActivity)
//                startActivity(startSettingsActivityIntent)
//                return true
//            }
//            R.id.action_db -> {
//                val startDbActviity = databaseActivityIntent
//                startActivityForResult(startDbActviity, SEARCH_ACTIVITY_REQUEST_CODE)
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    /**
//     * Helper method to add cards to Anki if permission is granted
//     * @param requestCode a signifier request code
//     * @param permissions the permissions desired
//     * @param grantResults if the permissions were granted
//     */
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
//                                            grantResults: IntArray) {
//        val message: String
//        val duration = Toast.LENGTH_SHORT
//        if (requestCode == ADD_PERM_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            val notes = mBinding!!.ankiAdditionalFields.etNotes.text.toString()
//            val wordContext = mBinding!!.ankiAdditionalFields.etContext.text.toString()
//            mAnkiDroid!!.addWordToAnki(mLastSearched, notes, wordContext)
//            message = getString(R.string.anki_added_toast)
//            showToast(message, duration)
//        } else {
//            message = getString(R.string.permissions_denied_toast)
//        }
//        showToast(message, duration)
//    }
//
//    private fun showToast(message: String, duration: Int) {
//        if (mToast != null) {
//            mToast!!.cancel()
//        }
//        val context = this@SearchActivity
//        mToast = Toast.makeText(context, message, duration)
//        mToast!!.show()
//    }
//
//    companion object {
//        val LOG_TAG = "Wanicchou"
//        private val ADD_PERM_REQUEST = 0
//        private val SEARCH_ACTIVITY_REQUEST_CODE = 42
//    }
//
//    //TODO: Change the context/notes/def to be live data objects


}
