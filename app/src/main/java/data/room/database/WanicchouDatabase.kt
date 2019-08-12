package data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import data.arch.util.SingletonHolder
import data.room.dao.composite.DictionaryEntryDao
import data.room.dao.composite.VocabularyAndTagDao
import data.room.dao.entity.*
import data.room.database.migration.WanicchouMigrationV2V3
import data.room.dbo.composite.VocabularyAndTag
import data.room.dbo.entity.*

/**
 * Database object using the Room Persistence Library.
 * Invoke the singleton by calling the class with a context
 */
@Database(
        entities = [
            Vocabulary::class,
            Definition::class,
            DefinitionNote::class,
            Dictionary::class,
            Tag::class,
            VocabularyNote::class,
            VocabularyTag::class,
            MatchType::class,
            DictionaryMatchType::class,
            Language::class,
            Translation::class
        ],
        views = [VocabularyAndTag::class],
        version = 3,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WanicchouDatabase : RoomDatabase() {
    abstract fun definitionDao(): DefinitionDao
    abstract fun definitionNoteDao(): DefinitionNoteDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun tagDao(): TagDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun vocabularyNoteDao(): VocabularyNoteDao
    abstract fun vocabularyTagDao(): VocabularyTagDao
    abstract fun matchTypeDao(): MatchTypeDao
    abstract fun dictionaryMatchTypeDao(): DictionaryMatchTypeDao
    abstract fun languageDao(): LanguageDao
    abstract fun translationDao(): TranslationDao
    abstract fun dictionaryEntryDao(): DictionaryEntryDao
    abstract fun vocabularyAndTagDao(): VocabularyAndTagDao

    companion object : SingletonHolder<WanicchouDatabase, Context>({
        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(WanicchouMigration.MIGRATION_1_2_QUERY)
            }
        }

        val enumLikeValueInsertDatabaseCallback = EnumLikeValueInsertDatabaseCallback(it)
        Room.databaseBuilder(it.applicationContext,
                             WanicchouDatabase::class.java,
                             "WanicchouDatabase")
                .addMigrations(MIGRATION_1_2, WanicchouMigrationV2V3)
                .addCallback(enumLikeValueInsertDatabaseCallback)
                .build()
    }) {
        operator fun invoke(context: Context) : WanicchouDatabase {
            return getInstance(context)
        }
        const val INSERTION_FAILED_ID = -1L // Room's return value for failed @Insert calls
    }
}