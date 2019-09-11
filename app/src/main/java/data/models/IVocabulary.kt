package data.models

import data.enums.Language

interface IVocabulary {
    val word : String
    val pronunciation : String
    val pitch : String
    val language : Language
}