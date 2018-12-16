package data.vocab.shared

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Suppress("PLUGIN_WARNING")
@Parcelize
class WordListEntry(val relatedWord: String = "",
                    val wordLanguageCode: String = "",
                    val definitionLanguageCode: String = "",
                    val link: String = "") : Parcelable
