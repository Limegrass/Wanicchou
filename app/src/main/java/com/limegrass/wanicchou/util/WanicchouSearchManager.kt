package com.limegrass.wanicchou.util

import android.content.Context
import android.net.ConnectivityManager
import data.arch.models.IDictionaryEntry
import data.arch.search.DictionarySearchBuilder
import data.arch.search.SearchRequest
import data.enums.Language
import data.enums.MatchType
import data.room.database.WanicchouDatabase
import data.room.repository.DictionaryEntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//TODO: Make this not garbage
class WanicchouSearchManager(context : Context) {
    private val repository = DictionaryEntryRepository(WanicchouDatabase(context))
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val sharedPreferences : WanicchouSharedPreferenceHelper = WanicchouSharedPreferenceHelper(context)

    suspend fun search(searchTerm : String,
                       vocabularyLanguage : Language = sharedPreferences.vocabularyLanguage,
                       definitionLanguage : Language = sharedPreferences.definitionLanguage,
                       dictionaryMatchType : MatchType = sharedPreferences.dictionaryMatchType,
                       databaseMatchType : MatchType = sharedPreferences.databaseMatchType) : List<IDictionaryEntry> {
        val searchBuilder = DictionarySearchBuilder()
        val databaseRequest = SearchRequest(searchTerm,
                vocabularyLanguage,
                definitionLanguage,
                dictionaryMatchType)
        searchBuilder.register(repository, databaseRequest)

        if(connectivityManager.activeNetworkInfo != null) {
            val dictionary = sharedPreferences.dictionary
            val dictionaryRequest = SearchRequest(searchTerm,
                    vocabularyLanguage,
                    definitionLanguage,
                    databaseMatchType)
            searchBuilder.register(dictionary, dictionaryRequest)
        }
        val searchResults = searchBuilder.executeSearches()
        if(searchResults.isNotEmpty()){
            GlobalScope.launch(Dispatchers.IO){
                for(result in searchResults){
                    repository.insert(result)
                }
            }
        }
        return searchResults
    }
}