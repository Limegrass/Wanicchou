package data.arch.models

interface INote<T> {
    val noteText : String
    val topic : T
}