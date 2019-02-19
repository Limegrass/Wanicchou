//package data.arch.vocab
//
//import com.waifusims.wanicchou.viewmodel.SearchViewModel
//import data.arch.search.IDictionaryWebPage
//import org.jsoup.nodes.Document
//
//class OnPageParsedAlwaysSaveDecorator(private val onPageParsed : IDictionaryWebPage.OnPageParsed,
//                                      private val viewModel : SearchViewModel,
//                                      private val repository: IVocabularyRepository)
//        : IDictionaryWebPage.OnPageParsed {
//    override fun onPageParsed(document: Document,
//                              wordLanguageCode: String,
//                              definitionLanguageCode: String,
//                              ) {
//        onPageParsed.onPageParsed(document,
//                     wordLanguageCode,
//                     definitionLanguageCode)
//        val vocabulary = viewModel.vocabulary
//        val definition = viewModel.definitions
//    }
//
//
//
//}