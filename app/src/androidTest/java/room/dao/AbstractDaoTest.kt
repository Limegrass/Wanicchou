package room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import room.database.WanicchouDatabase
import room.dbo.entity.Dictionary
import room.dbo.entity.Language
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.io.IOException

abstract class AbstractDaoTest {
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