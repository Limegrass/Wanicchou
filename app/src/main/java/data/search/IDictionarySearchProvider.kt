package data.search

import data.models.IDictionaryEntry
import data.architecture.ISearchProvider

interface IDictionarySearchProvider
    : IDictionarySource,
        ISearchProvider<List<IDictionaryEntry>, SearchRequest>
