package data.models

class DictionaryEntry (override val vocabulary: IVocabulary,
                       override val definitions: List<IDefinition> = listOf()) : IDictionaryEntry