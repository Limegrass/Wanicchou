package data.arch.search

import data.arch.models.IDictionaryEntry
import data.arch.util.ISearchProvider

interface IDictionarySearchProvider
    : IDictionarySource,
        ISearchProvider<List<IDictionaryEntry>, SearchRequest>
