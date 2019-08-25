package data.anki

import data.arch.models.IDefinition
import data.arch.models.IVocabulary

class WanicchouAnkiEntry(val vocabulary : IVocabulary,
                         val definition : IDefinition,
                         val notes : List<String>)