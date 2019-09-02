package data.search

import data.models.IDictionaryEntry
import data.architecture.ISearchProvider
import java.util.*

/**
 * Manages the order of search performed based on user preference.
 */
class DictionarySearchManager {
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

    companion object {
        private fun List<IDictionaryEntry>.isSuccess() : Boolean{
            return this.any { it.definitions.isNotEmpty() }
        }
    }
    private class SearchCombination(val searchProvider: ISearchProvider<List<IDictionaryEntry>, SearchRequest>,
                                    val searchRequest: SearchRequest)
}