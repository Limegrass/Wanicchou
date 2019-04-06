package data.arch.util

interface IFactory<T> {
    fun get() : T
}