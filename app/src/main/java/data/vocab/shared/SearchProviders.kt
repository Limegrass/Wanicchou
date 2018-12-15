package data.vocab.shared

import data.vocab.lang.jp.sanseidou.Sanseidou

/**
 * List all available search providers and a method to return
 * the class which defines all specific implementations of models
 * related to that search provider.
 */
object SearchProviders {
    private val PROVIDERS = arrayOf<Class<*>>(Sanseidou::class.java)

    /**
     * Returns the Class given the the class's name.
     * @param className the name of the provider class to return.
     * @return the Class associated with the given name if it exists, else null
     */
    fun getClassByKey(className: String): Class<*>? {
        for (provider in PROVIDERS) {
            if (provider.name == className) {
                return provider
            }
        }
        return null
    }
}
