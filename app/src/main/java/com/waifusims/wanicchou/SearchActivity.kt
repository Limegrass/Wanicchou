//<editor-fold desc="Imports">
package com.waifusims.wanicchou
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SearchEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.waifusims.wanicchou.ui.adapter.WordPagerAdapter
import com.waifusims.wanicchou.ui.fragments.FabFragment
import com.waifusims.wanicchou.ui.fragments.TabSwitchFragment
import com.waifusims.wanicchou.ui.fragments.WordFragment
import com.waifusims.wanicchou.util.WanicchouSharedPreferenceHelper
import com.waifusims.wanicchou.viewmodel.DefinitionViewModel
import com.waifusims.wanicchou.viewmodel.RelatedVocabularyViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.enums.AutoDelete
import data.room.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//</editor-fold>

//<editor-fold desc="TODO Notes">
//TODO: Don't save related words,
// just include an option for them to initiate an online search
//TODO: AutoImport to AnkiDroid if it exists
//TODO: Link related words by words that appear in definition
// TODO: Automatically select EJ for English input
//TODO:  Horizontal UI
// TODO: Toasts for DB searches
//TODO : Add click listener for Def label
//TODO: Figure out how to use UI fragments properly and split initialization up
//TODO: Search Suggestions
//</editor-fold>
class SearchActivity
    : AppCompatActivity()
//        , IVocabularyRepository.OnQueryFinish
{
    //<editor-fold desc="Fields/Properties">
    private val vocabularyViewModel: VocabularyViewModel by lazy {
        ViewModelProviders.of(this)
                .get(VocabularyViewModel::class.java)
    }

    private val definitionViewModel: DefinitionViewModel by lazy {
        ViewModelProviders.of(this)
                .get(DefinitionViewModel::class.java)
    }

    private val relatedVocabularyViewModel: RelatedVocabularyViewModel by lazy {
        ViewModelProviders.of(this)
                .get(RelatedVocabularyViewModel::class.java)
    }

    private val repository : VocabularyRepository by lazy {
        VocabularyRepository(this.application)
    }
    private val sharedPreferences
            : WanicchouSharedPreferenceHelper by lazy {
        WanicchouSharedPreferenceHelper(this)
    }

//    private val floatingActionButton: FloatingActionButton by lazy {
//        this.findViewById(R.id.fab) as FloatingActionButton
//    }

    companion object {
        private val TAG: String = SearchActivity::class.java.simpleName
    }

    private var toast: Toast? = null
    //</editor-fold>

    //<editor-fold desc="Activity LifeCycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wanicchou)

        val pager = findViewById<ViewPager>(R.id.pager)
        val tabDots = findViewById<TabLayout>(R.id.tab_dots)
        tabDots.setupWithViewPager(pager, true)
        pager.adapter = WordPagerAdapter(supportFragmentManager)
        val transaction = supportFragmentManager.beginTransaction()

        transaction.add(R.id.container_header, WordFragment())
        transaction.add(R.id.container_header, TabSwitchFragment())
//        transaction.add(R.id.container_body, DefinitionFragment())

        val fabFragment = FabFragment()
        transaction.add(R.id.container_frame, fabFragment)
        transaction.commit()
        setVocabularyObserver()
    }

    override fun onResume() {
        getLatest()
        super.onResume()
    }

    private fun getLatest() {
        GlobalScope.launch(Dispatchers.IO) {
            val vocabularyList = repository.getLatest()
            //TODO: Maybe refactor to just give the DICTIONARY_ID
            // (or when I figure out dynamic settings pref)
            runOnUiThread {
                vocabularyViewModel.wordIndex = 0
                vocabularyViewModel.vocabularyList = vocabularyList
            }
        }
    }

    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        Log.i(TAG, "SearchRequested: [$searchEvent].")
        return super.onSearchRequested(searchEvent)
    }

    override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "Received new intent. Action: [${intent.action}].")
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    //<editor-fold desc="Menu">
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        //TODO: Unsure if these were actually useful. Need to check and clean
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchableInfo = searchManager.getSearchableInfo(componentName)
//        searchView.setSearchableInfo(searchableInfo)
//        Log.i(TAG, "SearchableInfo: $searchableInfo")
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        val menuItem = menu.findItem(R.id.menu_search)
        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        Log.i(TAG, "Query text changed: [$newText].")
                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        Log.i(TAG, "Query text submitted: [$query].")
                        menuItem.collapseActionView()
                        return true
                    }
                })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                onSearchRequested()
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
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Helpers">


    private fun handleIntent(intent: Intent) {
        Log.i(TAG, "Handling Intent: [${intent.action}]")
        // This was recommended, but the search button caused Intent.ACTION_MAIN instead
        if (intent.action == Intent.ACTION_SEARCH) {
            val searchTerm = intent.getStringExtra(SearchManager.QUERY)

            search(searchTerm)
        }
    }


    private fun showToast(toastText: String) {
        val context = this
        toast = Toast.makeText(context,
                toastText,
                Toast.LENGTH_LONG)
        toast!!.show()
    }

    private fun setVocabularyObserver() {
        val lifecycleOwner = this
        vocabularyViewModel.setObserver(lifecycleOwner, ::setupDefinitionViewModel, null)
        vocabularyViewModel.setObserver(lifecycleOwner, ::setupRelatedVocabularyViewModel, null)
    }

    private fun search(searchTerm: String) {
        Log.i(TAG, "Search Initiated: [$searchTerm].")
        showToast("Searching for $searchTerm...")

        if (sharedPreferences.autoDelete == AutoDelete.ON_SEARCH) {
            repository.removeVocabulary(vocabularyViewModel.vocabulary)
        }
        //TODO: String template it
        //TODO: Progress bar it
//        val lifecycleOwner = this@SearchActivity
        runBlocking(Dispatchers.IO) {
            val vocabularyList = repository.vocabularySearch(searchTerm,
                    sharedPreferences.wordLanguageCode,
                    sharedPreferences.definitionLanguageCode,
                    sharedPreferences.matchType,
                    sharedPreferences.dictionary)
            if (vocabularyList.isNotEmpty()) {
                runOnUiThread {
                    vocabularyViewModel.vocabularyList = vocabularyList
                }
            }

        }
    }

    private fun setupDefinitionViewModel(@Suppress("UNUSED_PARAMETER") view: View?) {
        GlobalScope.launch(Dispatchers.IO) {
            val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
            val definitionList = repository.getDefinitions(vocabularyID,
                    sharedPreferences.definitionLanguageCode,
                    sharedPreferences.dictionary)
            runOnUiThread {
                definitionViewModel.definitionList = definitionList
            }
        }
    }

    private fun setupRelatedVocabularyViewModel(@Suppress("UNUSED_PARAMETER") view: View?) {
        GlobalScope.launch(Dispatchers.IO) {
            val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
            val relatedWordList = repository.getRelatedWords(vocabularyID)
            runOnUiThread {
                relatedVocabularyViewModel.relatedVocabularyList = relatedWordList
            }
        }
    }
    //</editor-fold>
}

