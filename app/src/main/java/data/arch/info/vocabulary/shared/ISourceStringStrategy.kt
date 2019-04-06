package data.arch.info.vocabulary.shared

import org.jsoup.nodes.Element

internal interface ISourceStringStrategy{
    fun getSource(htmlElement : Element) : String
}