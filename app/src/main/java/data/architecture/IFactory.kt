package data.architecture

interface IFactory<T> {
    fun get() : T
}