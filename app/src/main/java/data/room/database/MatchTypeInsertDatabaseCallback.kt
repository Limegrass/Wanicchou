package data.room.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import data.room.entity.MatchType
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class MatchTypeInsertDatabaseCallback(val context : Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Executors.newSingleThreadExecutor().execute {
            runBlocking{
                val wordEquals = MatchType("WORD_EQUALS", 1, "%s", 1)
                val wordStartsWith = MatchType("WORD_STARTS_WITH", 2, "%s%%", 2)
                val wordEndsWith = MatchType("WORD_ENDS_WITH", 4, "%%%s", 3)
                val wordContains = MatchType("WORD_CONTAINS", 8, "%%%s%%", 4)
                val wordWildCards = MatchType("WORD_WILDCARDS", 16, "%s", 5)
                val definitionContains = MatchType("DEFINITION_CONTAINS", 32, "%%%s%%", 6)
                val wordOrDefinitionContains = MatchType("WORD_OR_DEFINITION_CONTAINS", 64, "%%%s%%", 7)

                val dao = WanicchouDatabase.getInstance(context).matchTypeDao()

                dao.insert(wordEquals)
                dao.insert(wordStartsWith)
                dao.insert(wordEndsWith)
                dao.insert(wordContains)
                dao.insert(wordWildCards)
                dao.insert(definitionContains)
                dao.insert(wordOrDefinitionContains)
            }
        }
    }
}
