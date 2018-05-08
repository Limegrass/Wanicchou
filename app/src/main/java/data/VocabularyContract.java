package data;

import android.provider.BaseColumns;

/**
 * Created by Limegrass on 5/7/2018.
 */

public final class VocabularyContract {
    // Prevent people from making an object of the contract class
    private VocabularyContract() {}

    public static class VocabularyEntry implements BaseColumns{
        public static final String TABLE_NAME = "vocabulary";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_READING = "reading";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_PITCH = "pitch";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_CONTEXT = "context";
    }
}
