package data.models

import data.arch.models.IVocabulary
import data.enums.Language

class Vocabulary (override val word: String,
                  override val pronunciation: String,
                  override val pitch: String,
                  override val language: Language) : IVocabulary