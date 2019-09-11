package room.database

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import room.dbo.entity.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class EnumLikeValueInsertDatabaseCallback(private val context : Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        Executors.newSingleThreadExecutor().execute {
            val database = WanicchouDatabase(context)
            super.onCreate(db)
            runBlocking {
                insertLanguages(database)
            }
            runBlocking {
                insertDictionaries(database)
            }
            insertDefaultEntry(database)
        }
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

    private fun insertDefaultEntry(database: WanicchouDatabase){
        GlobalScope.launch {
            val defaultVocabulary = Vocabulary(
                    word = "和日帳",
                    pronunciation =  "わにっちょう",
                    pitch = "",
                    language = data.enums.Language.JAPANESE,
                    vocabularyID = 1)
            val defaultDefinition = Definition(
                    definitionText = """タイトルバーを押して、検索できる辞書アプリ。
Tap the title bar to begin entering a search term. 
Navigate to the settings screen from the menu bar at the top right. 
The floating action button will send the current definition to AnkiDroid. 
Tags will be imported to AnkiDroid. 
Vocabulary Notes will persist across the vocabulary term (for example, a note that is relevant to both
the Japanese-Japanese definition and the Japanese-English definition for a given word.) 
Tapping on dark grey boxes will bring up a window to edit the text. 
Tapping on an entry in Related will attempt to search that word. 
Report bugs at github.com/Limegrass/Wanicchou/issues. 
設定画面は右上隅のメニューに移られます。 
FABを押して、カードは暗記ドロイドに送ります。 
単語のメモはどんな定義でも、そのメモが出てきます。 
普通の灰色より暗い灰色のところを押せば、そのテキストのエディットボックスが現れます。 
バグが現れたら、github.com/Limegrass/Wanicchou/issues に新しいIssueを追加してください。""",
                    language = data.enums.Language.JAPANESE,
                    dictionary = data.enums.Dictionary.SANSEIDO,
                    vocabularyID = 1,
                    definitionID = 1)
            database.vocabularyDao().insert(defaultVocabulary)
            database.definitionDao().insert(defaultDefinition)
        }
    }
    //</editor-fold>
}