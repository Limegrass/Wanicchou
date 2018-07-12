package com.waifusims.wanicchou;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import data.vocab.jp.JapaneseDictionaryType;
import data.vocab.jp.search.sanseido.SanseidoMatchType;
import data.vocab.models.DictionaryType;
import data.vocab.models.MatchType;
import data.vocab.models.SearchProvider;
import data.vocab.models.SearchProviders;

public class WanicchouSharedPreferencesHelper {
    private Context context;

    WanicchouSharedPreferencesHelper(Context context){
        this.context = context;
    }

    /**
     * Gets a string from shared preferences. Returns an empty string if it doesn't exist.
     * @param resKey an integer resource key
     * @return the string associated with the key
     */
    public String getString(int resKey){
        String stringIfMissing = "";
        String sharedPrefKey = context.getString(resKey);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(sharedPrefKey, stringIfMissing);
    }

    public String getString(String sharedPrefKey){
        String stringIfMissing = "";
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(sharedPrefKey, stringIfMissing);
    }

    public String getString(int resKey, String stringIfMissing){
        String sharedPrefKey = context.getString(resKey);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(sharedPrefKey, stringIfMissing);
    }

    public String getString(int resKey, int missingResKey){
        String sharedPrefKey = context.getString(resKey);
        String stringIfMissing = context.getString(missingResKey);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(sharedPrefKey, stringIfMissing);
    }

    public String getString(String sharedPrefKey, String stringIfMissing){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(sharedPrefKey, stringIfMissing);
    }

    public void putString(int resKey, String stringToSave){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sharedPrefKey = context.getString(resKey);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPrefKey, stringToSave);
        editor.apply();
    }

    public void putString(String sharedPrefKey, String stringToSave){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPrefKey, stringToSave);
        editor.apply();
    }

    public SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String autoDeleteOption(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_auto_delete_key),
                context.getString(R.string.pref_auto_delete_default));
    }

    public String autoSaveOption(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_auto_save_key),
                context.getString(R.string.pref_auto_save_default));
    }

    public DictionaryType getDictionaryPreference(){
        return JapaneseDictionaryType.fromKey(
                getString(R.string.pref_dictionary_type_key, R.string.pref_dictionary_type_default)
        );
    }

    public MatchType getMatchType(){
        String matchTypeString = getString(R.string.pref_match_type_key, R.string.pref_match_type_default);
        return SanseidoMatchType.fromKey(matchTypeString);
    }

    public SearchProvider getSearchProvider(){
        String providerKey = getString(R.string.pref_provider_key, R.string.pref_provider_default);
        try {
            Class providerClass = SearchProviders.getClassByKey(providerKey);
            if(providerClass != null){
                Constructor<?> constructor = providerClass.getConstructor();
                return (SearchProvider) constructor.newInstance();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
