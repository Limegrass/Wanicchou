package data.models

import data.enums.Dictionary
import data.enums.Language

interface IDefinition {
    val definitionText : String
    val dictionary : Dictionary
    val language : Language
}