package data.arch.vocab

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.webkit.WebView
import data.arch.search.IDictionaryWebPage
import data.graveyard.DictionaryEntry
import data.enums.MatchType
import data.room.entity.*

interface IVocabularyRepository {
    fun getLatest(onQueryFinish: OnQueryFinish)

//    fun saveResults(dictionaryEntry: DictionaryEntry,
//                    relatedWords: List<WordListEntry>)

    fun search(searchTerm: String = "",
               wordLanguageCode: String,
               definitionLanguageCode: String,
               matchType: MatchType = MatchType.WORD_EQUALS,
               dictionary: String,
               lifecycleOwner: LifecycleOwner)

//    fun save(definition : Definition)
//    fun save(vocabulary : Vocabulary)
//    fun save(note : VocabularyNote)
//    fun save(note : DefinitionNote)
//    fun save(tag : Tag)
//    fun save(vocabularyRelation : VocabularyRelation)
//    fun save(tag : VocabularyTag)

    interface OnQueryFinish{
        fun onQueryFinish(vocabularyInformation: LiveData<List<VocabularyInformation>>)
    }


}