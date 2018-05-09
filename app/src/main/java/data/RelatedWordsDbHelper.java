package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Limegrass on 5/9/2018.
 */

public class RelatedWordsDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "WanicchouRelatedWords.db";
    private static final int DATABASE_VERSION = 1;

    public RelatedWordsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_RELATED_WORDS_TABLE =
                "CREATE TABLE "
                        + RelatedWordsContract.RelatedWordEntry.TABLE_NAME
                        + " ("
                        + RelatedWordsContract.RelatedWordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + RelatedWordsContract.RelatedWordEntry.COLUMN_RELATED_WORD + " VARCHAR(32) NOT NULL "
                        + "CONSTRAINT " + RelatedWordsContract.RelatedWordEntry.FK_BASE_WORD
                        + " FOREIGN KEY " + "(" + VocabularyContract.VocabularyEntry._ID + ") "
                        + "REFERENCES " + VocabularyContract.VocabularyEntry.TABLE_NAME
                        + "(" + VocabularyContract.VocabularyEntry._ID + ") "
                        + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_RELATED_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO: Alter instead of drop
        final String SQL_DROP_VOCAB_TABLE = "DROP TABLE IF EXISTS "
                + RelatedWordsContract.RelatedWordEntry.TABLE_NAME;

        sqLiteDatabase.execSQL(SQL_DROP_VOCAB_TABLE);

        onCreate(sqLiteDatabase);
    }
}
