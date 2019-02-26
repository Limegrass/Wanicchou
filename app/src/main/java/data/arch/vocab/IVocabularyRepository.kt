package data.arch.vocab

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import data.enums.MatchType
import data.room.entity.Vocabulary
import data.room.entity.VocabularyInformation

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