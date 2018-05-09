package data;

import android.provider.BaseColumns;

/**
 * Created by Limegrass on 5/7/2018.
 */

public final class VocabularyContract {
    // Prevent people from making an object of the contract class
    private VocabularyContract() {}

    public static class VocabularyEntry implements BaseColumns{
        public static final String TABLE_NAME = "VocabularyWords";
        public static final String COLUMN_WORD = "Word";
        public static final String COLUMN_READING = "Reading";
        public static final String COLUMN_DEFINITION = "Definition";
        public static final String COLUMN_DICTIONARY_TYPE = "DictionaryType";
        public static final String COLUMN_PITCH = "Pitch";
        public static final String COLUMN_NOTES = "Notes";
        public static final String COLUMN_CONTEXT = "Context";
    }
}
