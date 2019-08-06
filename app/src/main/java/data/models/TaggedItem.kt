package data.models

import data.arch.models.ITaggedItem

class TaggedItem<T>(override val item : T, override val tag: String)
    : ITaggedItem<T>