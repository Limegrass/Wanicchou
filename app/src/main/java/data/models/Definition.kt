package data.models

import data.enums.Dictionary
import data.enums.Language

class Definition (override val definitionText: String,
                  override val language: Language,
                  override val dictionary: Dictionary) : IDefinition