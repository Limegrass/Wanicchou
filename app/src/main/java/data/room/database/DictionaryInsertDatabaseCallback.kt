package data.room.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import data.room.entity.Dictionary
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class DictionaryInsertDatabaseCallback(val context : Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Executors.newSingleThreadExecutor().execute {
            runBlocking{
                val sanseidoAvailableBitMaskInclude = 47 //1 + 2 + 4 + 8 + 32
                val dictionaryID = 1L
                val sanseido = Dictionary("Sanseido",
                        sanseidoAvailableBitMaskInclude,
                        dictionaryID)

                WanicchouDatabase.getInstance(context).dictionaryDao().insert(sanseido)
            }
        }
    }
}