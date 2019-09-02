package room.dao.entity

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import room.database.WanicchouDatabase
import room.dbo.entity.Dictionary
import room.dbo.entity.Language
import room.dbo.entity.MatchType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.io.IOException

abstract class DaoTest {
    protected lateinit var db : WanicchouDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context,
                WanicchouDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        runBlocking {
            insertLanguages(db)
            insertMatchTypes(db)
        }
        runBlocking {
            insertDictionaries(db)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    //<editor-fold desc="Sub methods">
    private suspend fun insertLanguages(database: WanicchouDatabase){
        for (language in data.enums.Language.values()){
            val entity = Language(language.name, language.languageCode, language.languageID)
            database.languageDao().insert(entity)
        }
    }

    private suspend fun insertMatchTypes(database: WanicchouDatabase){
        val dao = database.matchTypeDao()
        for (matchType in data.enums.MatchType.values()) {
            val entity = MatchType(matchType.name,
                    matchType.templateString,
                    matchType.matchTypeID)
            dao.insert(entity)
        }
    }

    private suspend fun insertDictionaries(database: WanicchouDatabase){
        for (dictionary in data.enums.Dictionary.values()){
            val entity = Dictionary(dictionary.dictionaryName,
                    dictionary.defaultVocabularyLanguage,
                    dictionary.defaultVocabularyLanguage,
                    dictionary.dictionaryID)
            database.dictionaryDao().insert(entity)
        }
    }
}