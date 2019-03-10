//<editor-fold desc="Imports">
package com.waifusims.wanicchou
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SearchEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.waifusims.wanicchou.ui.fragments.DefinitionFragment
import com.waifusims.wanicchou.ui.fragments.FabFragment
import com.waifusims.wanicchou.ui.fragments.TabSwitchFragment
import com.waifusims.wanicchou.ui.fragments.WordFragment
import com.waifusims.wanicchou.util.WanicchouSharedPreferenceHelper
import com.waifusims.wanicchou.viewmodel.DefinitionViewModel
import com.waifusims.wanicchou.viewmodel.VocabularyViewModel
import data.arch.vocab.IVocabularyRepository
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

//    private val vocabularyViewModel: VocabularyViewModel by lazy {
//        ViewModelProviders.of(this)
//                .get(VocabularyViewModel::class.java)
//    }

    private val repository : IVocabularyRepository by lazy {
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
        private val TAG : String = SearchActivity::class.java.simpleName
    }
    private var toast : Toast? = null
    //</editor-fold>

    //<editor-fold desc="Activity LifeCycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wanicchou)

        val transaction = supportFragmentManager.beginTransaction()

        transaction.add(R.id.container_header, WordFragment())
        transaction.add(R.id.container_header, TabSwitchFragment())
        transaction.add(R.id.container_body, DefinitionFragment())

        val fabFragment = FabFragment()
        val bundle = Bundle()
        bundle.putParcelableArrayList(FabFragment.DICTIONARIES_BUNDLE_KEY,
                ArrayList(repository.dictionaries))
        fabFragment.arguments = bundle
        transaction.add(R.id.container_frame, fabFragment)
        transaction.commit()
    }

    override fun onResume() {
        getLatest()
        super.onResume()
    }
    private fun getLatest() {
        GlobalScope.launch(Dispatchers.IO) {
            val vocabularyList = repository.getLatest()
            //TODO: Maybe refactor to just give the dictionaryID
            // (or when I figure out dynamic settings pref)
            val definitionList = repository.getDefinitions(vocabularyList[0].vocabularyID,
                                                           sharedPreferences.definitionLanguageCode,
                                                           sharedPreferences.dictionary)
            runOnUiThread {
                vocabularyViewModel.resetWordIndex()
                vocabularyViewModel.setVocabularyList(vocabularyList)
                definitionViewModel.setDefinitionList(definitionList)
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
        if(intent.action == Intent.ACTION_SEARCH){
            val searchTerm = intent.getStringExtra(SearchManager.QUERY)

            search(searchTerm)
        }
    }


    private fun showToast(toastText : String){
        val context = this
        toast = Toast.makeText(context,
                toastText,
                Toast.LENGTH_LONG)
        toast!!.show()
    }

    private fun search(searchTerm: String) {
        Log.i(TAG, "Search Initiated: [$searchTerm].")
        showToast( "Searching for $searchTerm..." )

        if(sharedPreferences.autoDelete == AutoDelete.ON_SEARCH){
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
            if(vocabularyList.isNotEmpty()){
                val definitionList = repository.getDefinitions(vocabularyList[0].vocabularyID,
                        sharedPreferences.definitionLanguageCode,
                        sharedPreferences.dictionary)
                runOnUiThread {
                    vocabularyViewModel.setVocabularyList(vocabularyList)
                    definitionViewModel.setDefinitionList(definitionList)
                }
            }

        }

    }
    //</editor-fold>
}

