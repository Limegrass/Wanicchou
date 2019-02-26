package com.waifusims.wanicchou

import android.app.SearchManager
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SearchEvent
import android.widget.TextView
import android.widget.Toast
import com.waifusims.wanicchou.adapter.DefinitionAdapter
import com.waifusims.wanicchou.util.WanicchouSharedPreferenceHelper
import data.room.entity.Definition
import data.room.entity.Vocabulary
import data.arch.vocab.IVocabularyRepository
import data.room.VocabularyRepository
import com.waifusims.wanicchou.viewmodel.SearchViewModel
import data.arch.anki.AnkiDroidHelper
import data.arch.search.IDictionaryWebPage
import data.room.entity.VocabularyInformation
import data.search.SearchProvider
import org.jsoup.nodes.Document

//TODO: AutoImport to AnkiDroid if it exists
//TODO: Link related words by words that appear in definition
// TODO: Automatically select EJ for English input
//TODO:  Horizontal UI
// TODO: Toasts for DB searches
//TODO : Add click listener for Def label
//TODO: Figure out how to use UI fragments properly and split initialization up
class SearchActivity : AppCompatActivity() {

    companion object {
        private val TAG : String = SearchActivity::class.java.simpleName
    }

    //TODO: Loader or another Async Framework

    //TODO: Search Suggestions
    private lateinit var repository : IVocabularyRepository
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var sharedPreferences : WanicchouSharedPreferenceHelper
    private lateinit var ankiDroidHelper : AnkiDroidHelper
    private var toast : Toast? = null

    private val onQueryFinish = object : IVocabularyRepository.OnQueryFinish {
        override fun onQueryFinish(vocabularyInformation: LiveData<List<VocabularyInformation>>) {
            Log.d(TAG, "onQueryFinished")
            runOnUiThread{
                searchViewModel.setVocabularyInformation(vocabularyInformation)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
         menuInflater.inflate(R.menu.search_menu, menu)
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchableInfo = searchManager.getSearchableInfo(componentName)
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        val menuItem = menu.findItem(R.id.menu_search)
//        searchView.setSearchableInfo(searchableInfo)
        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        Log.i(TAG, "Query text changed: $newText")
                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        Log.i(TAG, "Query text submitted: $query")
                        searchView.clearFocus()
                        menuItem.collapseActionView()
                        return true
                    }
                })
//        Log.i(TAG, "SearchableInfo: $searchableInfo")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                onSearchRequested()
//                (item.actionView as SearchView).onActionViewExpanded()
                true
            }
            R.id.action_settings -> {
                val context = this
                val childActivity = SettingsActivity::class.java
                val settingsActivityIntent = Intent(context, childActivity)
                startActivity(settingsActivityIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wanicchou)
        val context = this
        sharedPreferences = WanicchouSharedPreferenceHelper(context)
        repository = VocabularyRepository(this.application, onQueryFinish)
        initializeViewModel()
        handleIntent(this.intent)
        //TODO: Don't initialize this until they request for a card
        ankiDroidHelper = AnkiDroidHelper(this)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.i(TAG, "Handling Intent: " + intent.action)
        // This was recommended, but the search button caused Intent.ACTION_MAIN instead
        if(intent.action == Intent.ACTION_SEARCH){
            val searchTerm = intent.getStringExtra(SearchManager.QUERY)
            search(searchTerm)
        }

    }

    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        Log.i(TAG, "SearchRequested: $searchEvent")
        return super.onSearchRequested(searchEvent)
    }

    private fun search(searchTerm: String){
        Log.i(TAG, "Search Initiated: $searchTerm")
        val lifecycleOwner = this
        repository.search(searchTerm,
                sharedPreferences.wordLanguageCode,
                sharedPreferences.definitionLanguageCode,
                sharedPreferences.matchType,
                sharedPreferences.dictionary,
                lifecycleOwner)
    }

    private fun initializeViewModel(){
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        setWordObserver()
    }

    private fun setWordObserver(){
        val tvWord = findViewById<TextView>(R.id.tv_word)
        val tvPronunciation = findViewById<TextView>(R.id.tv_pronunciation)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_definitions)
        val context = this@SearchActivity

        //TODO: Reset the wordIndex on new search
        val wordObserver = Observer<List<VocabularyInformation>>{
            if(it != null && it.isNotEmpty()){
                tvWord.text = it[searchViewModel.getWordIndex()].vocabulary!!.word
                tvPronunciation.text = it[searchViewModel.getWordIndex()].vocabulary!!.pronunciation

                recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                recyclerView.adapter = DefinitionAdapter(it[searchViewModel.getWordIndex()].definitions)
            }
        }

        val lifecycleOwner = this
        searchViewModel.setVocabularyInformationObserver(lifecycleOwner, wordObserver)
    }
}
