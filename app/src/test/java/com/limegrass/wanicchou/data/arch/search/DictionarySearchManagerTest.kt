package com.limegrass.wanicchou.data.arch.search

import data.arch.models.IDictionaryEntry
import data.arch.search.DictionarySearchManager
import data.arch.search.SearchRequest
import data.arch.util.ISearchProvider
import data.enums.Language
import data.enums.MatchType
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DictionarySearchManagerTest {

    @Test
    fun `executeSearches calls provider's search`(){
        val searchManager = DictionarySearchManager()
        val mockProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val searchRequest = SearchRequest("Test",
                                          Language.ENGLISH,
                                          Language.JAPANESE,
                                          MatchType.WORD_EQUALS)
        coEvery {
            mockProvider.search(searchRequest)
        } returns listOf()
        searchManager.register(mockProvider, searchRequest)
        runBlocking(Dispatchers.IO){
            searchManager.executeSearches()
        }
        coVerifyAll {
            mockProvider.search(searchRequest)
        }
    }

    @Test
    fun `executeSearches calls registered searches in order`() {
        val searchManager = DictionarySearchManager()
        val firstProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val secondProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val searchRequest = SearchRequest("Test",
                Language.ENGLISH,
                Language.JAPANESE,
                MatchType.WORD_EQUALS)
        coEvery {
            firstProvider.search(searchRequest)
        } returns listOf()
        coEvery {
            secondProvider.search(searchRequest)
        } returns listOf()
        searchManager.register(firstProvider, searchRequest)
        searchManager.register(secondProvider, searchRequest)
        runBlocking(Dispatchers.IO){
            searchManager.executeSearches()
        }
        coVerifySequence {
            firstProvider.search(searchRequest)
            secondProvider.search(searchRequest)
        }
    }

    @Test
    fun `executeSearches returns first successful result`() {
        val searchManager = DictionarySearchManager()
        val firstProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val secondProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val thirdProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val searchRequest = SearchRequest("Test",
                Language.ENGLISH,
                Language.JAPANESE,
                MatchType.WORD_EQUALS)

        coEvery {
            firstProvider.search(searchRequest)
        } returns listOf()
        coEvery {
            secondProvider.search(searchRequest)
        } returns listOf(mockk{
            every { definitions.isNotEmpty() } returns true
        })
        coEvery {
            thirdProvider.search(searchRequest)
        } returns listOf(mockk{
            every { definitions.isNotEmpty() } returns true
        })

        searchManager.register(firstProvider, searchRequest)
        searchManager.register(secondProvider, searchRequest)
        searchManager.register(thirdProvider, searchRequest)

        runBlocking(Dispatchers.IO){
            searchManager.executeSearches()
        }
        coVerifyAll {
            firstProvider.search(searchRequest)
            secondProvider.search(searchRequest)
        }
    }

    @Test
    fun `executeSearches returns empty list if all fails`() {
        val searchManager = DictionarySearchManager()
        val firstProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val secondProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val thirdProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val searchRequest = SearchRequest("Test",
                Language.ENGLISH,
                Language.JAPANESE,
                MatchType.WORD_EQUALS)

        coEvery {
            firstProvider.search(searchRequest)
        } returns listOf()
        coEvery {
            secondProvider.search(searchRequest)
        } returns listOf()
        coEvery {
            thirdProvider.search(searchRequest)
        } returns listOf()

        searchManager.register(firstProvider, searchRequest)
        searchManager.register(secondProvider, searchRequest)
        searchManager.register(thirdProvider, searchRequest)
        runBlocking(Dispatchers.IO){
            searchManager.executeSearches()
        }
        coVerifyAll {
            firstProvider.search(searchRequest)
            secondProvider.search(searchRequest)
        }
    }

    @Test
    fun `executeSearches uses registered request objects`() {
        val searchManager = DictionarySearchManager()
        val firstProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val secondProvider = mockk<ISearchProvider<List<IDictionaryEntry>, SearchRequest>>()
        val firstSearchRequest = SearchRequest("Test",
                Language.ENGLISH,
                Language.JAPANESE,
                MatchType.WORD_EQUALS)

        val secondSearchRequest = SearchRequest("Test",
                Language.ENGLISH,
                Language.JAPANESE,
                MatchType.WORD_EQUALS)


        coEvery {
            firstProvider.search(firstSearchRequest)
        } returns listOf()
        coEvery {
            secondProvider.search(secondSearchRequest)
        } returns listOf()

        searchManager.register(firstProvider, firstSearchRequest)
        searchManager.register(secondProvider, secondSearchRequest)
        runBlocking(Dispatchers.IO){
            searchManager.executeSearches()
        }
        coVerifyAll {
            firstProvider.search(firstSearchRequest)
            secondProvider.search(secondSearchRequest)
        }
    }
}