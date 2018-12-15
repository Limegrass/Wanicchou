package data.vocab.shared

import android.os.Parcelable

import java.net.URL

import kotlinx.android.parcel.Parcelize

@Suppress("PLUGIN_WARNING")
@Parcelize
class WordListEntry : Parcelable {
    val relatedWord: String = ""
    val link: String = ""

//    constructor(relatedWord: String, dictionaryTypeString: String) : this() {
//        this.relatedWord = relatedWord
//        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString)!!
//        //TODO: Handle no links for DB searches
//        this.link = ""
//    }
//
//    constructor(relatedWord: String, dictionaryType: DictionaryType, link: String) : this() {
//        this.relatedWord = relatedWord
//        this.dictionaryType = dictionaryType
//        this.link = link
//    }
//
//    constructor(relatedWord: String, dictionaryTypeString: String, link: String) : this() {
//        this.relatedWord = relatedWord
//        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString)!!
//        this.link = link
//    }
//
//    constructor(relatedWord: String, dictionaryType: DictionaryType, link: URL) : this() {
//        this.relatedWord = relatedWord
//        this.dictionaryType = dictionaryType
//        this.link = link.toString()
//    }
//
//    constructor(relatedWord: String, dictionaryTypeString: String, link: URL) : this() {
//        this.relatedWord = relatedWord
//        this.dictionaryType = JapaneseDictionaryType.fromKey(dictionaryTypeString)!!
//        this.link = link.toString()
//    }
//
//    fun setDictionaryType(dictionaryType: JapaneseDictionaryType) {
//        this.dictionaryType = dictionaryType
//    }
}
