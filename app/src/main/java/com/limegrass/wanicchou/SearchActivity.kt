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
import com.limegrass.wanicchou.util.WanicchouSharedPreferenceHelper
import com.limegrass.wanicchou.viewmodel.VocabularyViewModel
import data.enums.AutoDelete
import data.room.VocabularyRepository
import data.room.entity.Vocabulary
import kotlinx.coroutines.Dispatchers
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
        transaction.add(R.id.container_frame, FabFragment())
        transaction.commit()
    }

    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        Log.i(TAG, "SearchRequested: [$searchEvent].")
        return super.onSearchRequested(searchEvent)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.lastSearchedVocabularyID =
                vocabularyViewModel.vocabulary.vocabularyID
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
                vocabularyViewModel.value = listOf(vocab)
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
        toast?.cancel()
        toast = Toast.makeText(context,
                toastText,
                Toast.LENGTH_LONG)
        toast!!.show()
    }

    private fun search(searchTerm: String) {
        Log.i(TAG, "Search Initiated: [$searchTerm].")
        showToast(getString(R.string.word_searching, searchTerm))

        if (sharedPreferences.autoDelete == AutoDelete.ON_SEARCH) {
            repository.removeVocabulary(vocabularyViewModel.vocabulary)
        }
        //TODO: String template it
        //TODO: Progress bar it
        runBlocking(Dispatchers.IO) {
            val databaseList = repository.databaseSearch(searchTerm,
                    sharedPreferences.wordLanguageID,
                    sharedPreferences.definitionLanguageID,
                    sharedPreferences.databaseMatchType)
            if (databaseList.isNotEmpty()) {
                runOnUiThread {
                    val message = getString(R.string.toast_found_in_db, searchTerm)
                    showToast(message)
                    vocabularyViewModel.value = databaseList
                }
            }
            else {
                val connectivityManager =
                        this@SearchActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                if(networkInfo != null){
                    val onlineResult = repository.onlineSearch(searchTerm,
                            sharedPreferences.wordLanguageID,
                            sharedPreferences.definitionLanguageID,
                            sharedPreferences.dictionaryMatchType,
                            sharedPreferences.dictionary)
                    if (onlineResult.isNotEmpty()) {
                        runOnUiThread {
                            val message = getString(R.string.word_search_success, searchTerm)
                            showToast(message)
                            vocabularyViewModel.value = onlineResult
                        }
                    }
                    else{
                        runOnUiThread {
                            val message = getString(R.string.word_search_failure, searchTerm)
                            showToast(message)
                        }
                    }
                }
                else{
                    runOnUiThread {
                        val message = getString(R.string.toast_no_network)
                        showToast(message)
                    }
                }
            }
        }
    }
    //</editor-fold>
}

