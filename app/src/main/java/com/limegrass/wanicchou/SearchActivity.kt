//<editor-fold desc="Imports">
package com.limegrass.wanicchou
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SearchEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.limegrass.wanicchou.ui.fragments.FabFragment
import com.limegrass.wanicchou.ui.fragments.TabSwitchFragment
import com.limegrass.wanicchou.ui.fragments.WordFragment
import com.limegrass.wanicchou.util.WanicchouSearchManager
import com.limegrass.wanicchou.util.WanicchouSharedPreferences
import com.limegrass.wanicchou.util.cancelSetAndShowWanicchouToast
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel
import data.enums.MatchType
import room.database.WanicchouDatabase
import room.dbo.entity.Vocabulary
import room.repository.DictionaryEntryRepository
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
    private lateinit var connectivityManager : ConnectivityManager
    companion object {
        private val TAG: String = SearchActivity::class.java.simpleName
    }

    private lateinit var menu : Menu


    private val dictionaryEntryViewModel : DictionaryEntryViewModel by lazy {
        ViewModelProviders.of(this)
                .get(DictionaryEntryViewModel::class.java)
    }

    private val sharedPreferences : WanicchouSharedPreferences by lazy {
        WanicchouSharedPreferences(this)
    }

    private val searchManager by lazy {
        val database = WanicchouDatabase(this)
        val repository = DictionaryEntryRepository(database)
        WanicchouSearchManager(repository,
                connectivityManager,
                sharedPreferences,
                this)
    }

    //</editor-fold>

    //<editor-fold desc="Activity LifeCycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wanicchou)
        dictionaryEntryViewModel.value = sharedPreferences.lastDictionaryEntry

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container_header, WordFragment())
        transaction.add(R.id.container_header, TabSwitchFragment())
        transaction.add(R.id.container_frame, FabFragment())
        transaction.commit()
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        Log.i(TAG, "SearchRequested: [$searchEvent].")
        return super.onSearchRequested(searchEvent)
    }

    override fun onPause() {
        super.onPause()
        val dictionaryEntry = dictionaryEntryViewModel.value
        if(dictionaryEntry != null){
            sharedPreferences.lastDictionaryEntry = dictionaryEntry
        }
    }

    override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "Received new intent. Action: [${intent.action}].")
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == DatabaseActivity.REQUEST_CODE) {
            val vocab = data?.extras?.getParcelable<Vocabulary>("Vocabulary")
            if(vocab != null){
                GlobalScope.launch(Dispatchers.IO) {
                    searchManager.search(vocab.word,
                                         vocab.language,
                                         sharedPreferences.definitionLanguage,
                                         MatchType.WORD_EQUALS,
                                         MatchType.WORD_EQUALS)
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
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
            R.id.action_database -> {
                val context = this
                val childActivity = DatabaseActivity::class.java
                val settingsActivityIntent = Intent(context, childActivity)
                startActivityForResult(settingsActivityIntent, DatabaseActivity.REQUEST_CODE)
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
        val context = this@SearchActivity
        cancelSetAndShowWanicchouToast(context, toastText, Toast.LENGTH_LONG)
    }

    private fun search(searchTerm: String) {
        Log.i(TAG, "Search Initiated: [$searchTerm].")
        showToast(getString(R.string.word_searching, searchTerm))
        //TODO: Progress bar it
        runBlocking(Dispatchers.IO) {
            searchManager.search(searchTerm)
        }
    }
    //</editor-fold>
}