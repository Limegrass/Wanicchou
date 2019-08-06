package data.models

import data.arch.models.IDefinition
import data.arch.models.IDictionaryEntry
import data.arch.models.IVocabulary

class DictionaryEntry (override val vocabulary: IVocabulary,
                       override val definitions: List<IDefinition> = listOf()) : IDictionaryEntry