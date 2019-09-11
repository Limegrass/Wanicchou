package data.models

interface INote<T> {
    val noteText : String
    val topic : T
}