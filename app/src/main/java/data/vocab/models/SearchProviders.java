package data.vocab.models;

import data.vocab.jp.search.sanseido.Sanseido;

public class SearchProviders {
    public static final Class[] PROVIDERS = {Sanseido.class};

    public static Class getClassByKey(String key){
        for(Class provider : PROVIDERS){
            if(provider.getName().equals(key)){
                return provider;
            }
        }
        return null;
    }
}
