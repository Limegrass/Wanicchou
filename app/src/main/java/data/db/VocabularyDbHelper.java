package data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import data.db.VocabularyContract.*;

/**
 * Created by Limegrass on 5/7/2018.
 */

public class VocabularyDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "WanicchouVocab.db";

    private static final int DATABASE_VERSION = 4;

    public VocabularyDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_VOCAB_TABLE =
                "CREATE TABLE "
                        + VocabularyEntry.TABLE_NAME
                        + " ("
                        + VocabularyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        // Allow null for failed searches so I can save failed searches
                        // as well as inconsistent data
                        + VocabularyEntry.COLUMN_WORD + " VARCHAR(32),"
                        + VocabularyEntry.COLUMN_READING + " VARCHAR(128), "
                        + VocabularyEntry.COLUMN_DEFINITION + " VARCHAR(4096), "
                        + VocabularyEntry.COLUMN_CONTEXT + " VARCHAR(4096), "
                        + VocabularyEntry.COLUMN_NOTES + " VARCHAR(4096), "
                        // Could be an int, but all usages would just
                        // use strings and
                        // avoids full/half width troubles
                        + VocabularyEntry.COLUMN_PITCH + " VARCHAR(8), "
                        + VocabularyEntry.COLUMN_DICTIONARY_TYPE + " VARCHAR(8)"
                        + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_VOCAB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO: Alter instead of drop
        final String SQL_DROP_VOCAB_TABLE = "DROP TABLE IF EXISTS " + VocabularyEntry.TABLE_NAME;

        sqLiteDatabase.execSQL(SQL_DROP_VOCAB_TABLE);

        onCreate(sqLiteDatabase);

    }
}
