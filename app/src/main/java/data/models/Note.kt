package data.models

class Note<T>(override val topic: T,
              override val noteText: String)
    : INote<T>