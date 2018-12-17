package data.vocab.shared

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Suppress("PLUGIN_WARNING")
@Parcelize
class WordListEntry(val relatedWord: String = "",
                    val languageCode: String = "",
                    val link: String = "") : Parcelable
