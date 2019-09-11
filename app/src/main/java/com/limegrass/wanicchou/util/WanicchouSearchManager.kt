package com.limegrass.wanicchou.util

import android.net.ConnectivityManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.enums.AutoDelete
import com.limegrass.wanicchou.viewmodel.DictionaryEntryViewModel
import data.models.IDictionaryEntry
import data.search.DictionarySearchManager
import data.search.SearchRequest
import data.architecture.IRepository
import data.enums.Language
import data.enums.MatchType
import data.web.DictionarySearchProviderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//FIXME: Make this not garbage
//  Damn it became even worse
//  Decorator to handle deletion,
//  Decorator/Interface to handle connectivity checking
//  Handling the toast elsewhere
class WanicchouSearchManager(private val repository : IRepository<IDictionaryEntry, SearchRequest>,
                             private val connectivityManager: ConnectivityManager,
                             private val sharedPreferences : WanicchouSharedPreferences,
                             private val activity : FragmentActivity) {
    private val dictionaryEntryViewModel: DictionaryEntryViewModel by lazy {
        ViewModelProviders.of(activity)
                .get(DictionaryEntryViewModel::class.java)
    }

    suspend fun search(searchTerm : String,
                       vocabularyLanguage : Language = sharedPreferences.vocabularyLanguage,
                       definitionLanguage : Language = sharedPreferences.definitionLanguage,
                       dictionaryMatchType : MatchType = sharedPreferences.dictionaryMatchType,
                       databaseMatchType : MatchType = sharedPreferences.databaseMatchType) : List<IDictionaryEntry> {

        activity.runOnUiThread{
            Toast.makeText(activity,
                    activity.getString(R.string.word_searching,
                            searchTerm),
                    Toast.LENGTH_LONG).show()
        }
        val searchManager = DictionarySearchManager()
        val databaseRequest = SearchRequest(searchTerm,
                vocabularyLanguage,
                definitionLanguage,
                dictionaryMatchType)
        searchManager.register(repository, databaseRequest)

        if(connectivityManager.activeNetworkInfo != null) {
            val dictionary = sharedPreferences.dictionary
            val dictionaryRequest = SearchRequest(searchTerm,
                    vocabularyLanguage,
                    definitionLanguage,
                    databaseMatchType)
            val dictionarySource = DictionarySearchProviderFactory(dictionary).get()
            searchManager.register(dictionarySource, dictionaryRequest)
        }
        val searchResults = searchManager.executeSearches()
        if(searchResults.isNotEmpty()){
            GlobalScope.launch(Dispatchers.IO){
                val oldDictionaryEntry = dictionaryEntryViewModel.value
                activity.runOnUiThread {
                    dictionaryEntryViewModel.availableDictionaryEntries = searchResults
                }
                val dictionaryEntry = dictionaryEntryViewModel.value!!
                if (oldDictionaryEntry != null
                        && sharedPreferences.autoDelete == AutoDelete.ON_SEARCH){
                    repository.delete(oldDictionaryEntry)
                }
                activity.runOnUiThread {
                    val message = activity.getString(R.string.word_search_success,
                            searchTerm,
                            dictionaryEntry.definitions[0].dictionary.dictionaryName)
                    cancelSetAndShowWanicchouToast(activity, message, Toast.LENGTH_LONG)
                }
                for(result in searchResults){
                    repository.insert(result)
                }
            }

        }
        else {
            activity.runOnUiThread{
                val message = activity.getString(R.string.word_search_failure, searchTerm)
                cancelSetAndShowWanicchouToast(activity, message, Toast.LENGTH_LONG)
            }
        }
        return searchResults
    }
}