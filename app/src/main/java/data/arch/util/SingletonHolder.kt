package data.arch.util

open class SingletonHolder<out T, in A> (creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null
    fun getInstance(arg: A) : T {
        val localInstance = instance
        if (localInstance != null) {
            return localInstance
        }

        return synchronized(this){
            val syncedInstance = instance
            if (syncedInstance != null) {
                syncedInstance
            }
            else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

