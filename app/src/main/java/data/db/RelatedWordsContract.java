package data.db;

import android.provider.BaseColumns;

/**
 * Created by Limegrass on 5/9/2018.
 */

public class RelatedWordsContract {
    private RelatedWordsContract() {}

    public static class RelatedWordEntry implements BaseColumns {
        public static final String TABLE_NAME = "RelatedWords";
        public static final String FK_BASE_WORD = "FKBaseWord";
        public static final String COLUMN_RELATED_WORD = "RelatedWord";
        public static final String COLUMN_DICTIONARY_TYPE = "DictionaryType";
    }
}
