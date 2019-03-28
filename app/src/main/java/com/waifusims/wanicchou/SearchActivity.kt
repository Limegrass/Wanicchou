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
import androidx.lifecycle.ViewModelProviders
import com.waifusims.wanicchou.ui.fragments.*
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
//TODO: Don't save related words, just include an option for them to initiate an online search
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
{
    //<editor-fold desc="Fields/Properties">
    private lateinit var menu : Menu

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

    companion object {
        private val TAG: String = SearchActivity::class.java.simpleName
    }

    private var toast: Toast? = null
    //</editor-fold>

    //<editor-fold desc="Activity LifeCycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wanicchou)

        val transaction = supportFragmentManager.beginTransaction()

        transaction.add(R.id.container_header, WordFragment())
        transaction.add(R.id.container_header, TabSwitchFragment())

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
                vocabularyViewModel.list = vocabularyList
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
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                // Brings up the search dialog.
                // If voice/etc is implemented in the future, make sure it works with this.
                // Else need to remove this and add the actionViewClass onto the menu item.
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
        vocabularyViewModel.setObserver(lifecycleOwner){
            GlobalScope.launch(Dispatchers.IO) {
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                val definitionList = repository.getDefinitions(vocabularyID,
                        sharedPreferences.definitionLanguageCode,
                        sharedPreferences.dictionary)
                runOnUiThread {
                    definitionViewModel.list = definitionList
                }
            }
        }

        vocabularyViewModel.setObserver(lifecycleOwner){
            GlobalScope.launch(Dispatchers.IO) {
                val vocabularyID = vocabularyViewModel.vocabulary.vocabularyID
                val relatedWordList = repository.getRelatedWords(vocabularyID)
                runOnUiThread {
                    relatedVocabularyViewModel.list = relatedWordList
                }
            }
        }
    }

    private fun search(searchTerm: String) {
        Log.i(TAG, "Search Initiated: [$searchTerm].")
        showToast("Searching for $searchTerm...")

        if (sharedPreferences.autoDelete == AutoDelete.ON_SEARCH) {
            repository.removeVocabulary(vocabularyViewModel.vocabulary)
        }
        //TODO: String template it
        //TODO: Progress bar it
        runBlocking(Dispatchers.IO) {
            val vocabularyList = repository.vocabularySearch(searchTerm,
                    sharedPreferences.wordLanguageCode,
                    sharedPreferences.definitionLanguageCode,
                    sharedPreferences.matchType,
                    sharedPreferences.dictionary)
            if (vocabularyList.isNotEmpty()) {
                runOnUiThread {
                    vocabularyViewModel.list = vocabularyList
                }
            }

        }
    }
    //</editor-fold>
}

