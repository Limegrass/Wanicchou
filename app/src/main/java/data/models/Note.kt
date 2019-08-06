package data.models

import data.arch.models.INote

class Note<T>(override val topic: T,
              override val noteText: String)
    : INote<T>