package data.models

class TaggedItem<T>(override val item : T, override val tag: String)
    : ITaggedItem<T>