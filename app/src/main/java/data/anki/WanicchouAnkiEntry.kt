package data.anki

import data.models.IDefinition
import data.models.IVocabulary

class WanicchouAnkiEntry(val vocabulary : IVocabulary,
                         val definition : IDefinition,
                         val notes : List<String>)