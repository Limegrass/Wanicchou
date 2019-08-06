package data.arch.models

interface ITaggedItem<T> {
    val tag : String
    val item : T
}