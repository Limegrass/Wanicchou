package data.arch.search

import data.arch.models.IDictionaryEntry
import data.arch.util.ISearchProvider
import data.web.DictionarySourceFactory
import java.util.*

/**
 * Manages the order of search performed based on user preference.
 */
class DictionarySearchBuilder {
    private val searchCombinations : Queue<SearchCombination> = ArrayDeque()
    suspend fun executeSearches(): List<IDictionaryEntry> {
        while (searchCombinations.isNotEmpty()){
            val combination = searchCombinations.poll()
            val result = combination.searchProvider.search(combination.searchRequest)
            if (result.isSuccess()){
                return result
            }
        }
        return listOf()
    }

    fun register(searchProvider: ISearchProvider<List<IDictionaryEntry>, SearchRequest>,
                 searchRequest: SearchRequest) {
        val combination = SearchCombination(searchProvider, searchRequest)
        searchCombinations.add(combination)
    }

    fun register(dictionary : data.enums.Dictionary, searchRequest: SearchRequest) {
        val dictionarySource = DictionarySourceFactory(dictionary).get()
        val combination = SearchCombination(dictionarySource, searchRequest)
        searchCombinations.add(combination)
    }

    companion object {
        private fun List<IDictionaryEntry>.isSuccess() : Boolean{
            return this.any { it.definitions.isNotEmpty() }
        }
    }
    private class SearchCombination(val searchProvider: ISearchProvider<List<IDictionaryEntry>, SearchRequest>,
                                    val searchRequest: SearchRequest)
}