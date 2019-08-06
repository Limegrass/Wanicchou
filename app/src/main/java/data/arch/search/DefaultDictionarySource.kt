//package data.arch.search
//
//import data.arch.models.IDictionaryEntry
//import data.enums.Dictionary
//import data.enums.Language
//import data.enums.MatchType
//import data.models.Definition
//import data.models.DictionaryEntry
//import data.models.Vocabulary
//
////TODO: Use it and namespace it elsewhere
//class DefaultDictionarySource : IDictionarySource {
//    override val supportedMatchTypes: Set<MatchType>
//        get() = MatchType.values().toSet()
//    override val supportedTranslations: Map<Language, Set<Language>>
//        get() = mapOf(Language.JAPANESE to setOf(Language.JAPANESE))
//
//    override suspend fun search(request: SearchRequest): List<IDictionaryEntry> {
//        return listOf(DEFAULT_DICTIONARY_ENTRY)
//    }
//    companion object {
//        private val DEFAULT_VOCABULARY = Vocabulary(
//                word = "和日帳",
//                pronunciation =  "わにっちょう",
//                pitch = "",
//                language = Language.JAPANESE)
//        private val DEFAULT_DEFINITION = Definition(
//                definitionText ="ある使えないアプリ。",
//                language = Language.JAPANESE,
//                dictionary = Dictionary.SANSEIDO)
//        private val DEFAULT_DICTIONARY_ENTRY = DictionaryEntry(
//                DEFAULT_VOCABULARY,
//                listOf(DEFAULT_DEFINITION))
//    }
//
//}