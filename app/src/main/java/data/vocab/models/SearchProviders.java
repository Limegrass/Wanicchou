package data.vocab.models;

import data.vocab.jp.search.sanseido.Sanseido;

/**
 * List all available search providers and a method to return
 * the class which defines all specific implementations of models
 * related to that search provider.
 */
public class SearchProviders {
    private static final Class[] PROVIDERS = {Sanseido.class};

    /**
     * Returns the Class given the the class's name.
     * @param className the name of the provider class to return.
     * @return the Class associated with the given name if it exists, else null
     */
    public static Class getClassByKey(String className){
        for(Class provider : PROVIDERS){
            if(provider.getName().equals(className)){
                return provider;
            }
        }
        return null;
    }
}
