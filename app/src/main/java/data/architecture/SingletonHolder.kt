package data.architecture

open class SingletonHolder<out T, in A> (private val creator: (A) -> T) {
    @Volatile private var instance: T? = null
    // Double checked locking on the creator
    protected fun getInstance(arg: A) : T {
        instance = instance ?: synchronized(creator){
            instance = instance ?: creator(arg)
            instance
        }
        return instance!!
    }
}