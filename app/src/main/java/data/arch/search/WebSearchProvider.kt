package data.arch.search

import data.arch.models.IDictionaryEntry
import data.arch.util.ISearchProvider
import data.enums.Dictionary
import data.models.DictionaryEntry
import data.web.DictionarySourceFactory

// I can take in a IWebPage on construction (taking in the base Repo)
// I can also decorate it with some type of IDatabaseSaver ?? Maybe not because I'd need database access
// I can also assign a search strategy by matchtype
// This can allow me to decouple the strategy for the database and the strategy for online
// That way the SearchRequest is a raw combination of term and language
// Maybe instead of being a decorator it should just be completely separate from the repository
// There can be a visitor to save the data if needed
// I'll want an abstraction layer over it though
// I need to return related vocabulary though
class WebSearchProvider(private val dictionary : Dictionary)
    : ISearchProvider<List<IDictionaryEntry>, SearchRequest> {
    override suspend fun search(request: SearchRequest): List<IDictionaryEntry> {
        val source = DictionarySourceFactory(dictionary).get()
        return source.search(request)
    }
}