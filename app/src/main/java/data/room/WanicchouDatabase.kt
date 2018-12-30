package data.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.content.Context
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import data.arch.util.SingletonHolder
import data.room.dao.*
import data.room.entity.*

/**
 * Database object using the Room Persistence Library.
 * Singleton design to avoid multiple instances of database
 * connection when it is not needed.
 */
@Database(
        entities = [
            Vocabulary::class,
            Definition::class,
            DefinitionNote::class,
            Dictionary::class,
            Tag::class,
            VocabularyNote::class,
            VocabularyRelation::class,
            VocabularyTag::class
        ],
        version = 2
)
@TypeConverters(Converters::class)
abstract class WanicchouDatabase : RoomDatabase() {
    abstract fun definitionDao(): DefinitionDao
    abstract fun definitionNoteDao(): DefinitionNoteDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun tagDao(): TagDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun vocabularyNoteDao(): VocabularyNoteDao
    abstract fun vocabularyRelationDao(): VocabularyRelationDao
    abstract fun vocabularyTagDao(): VocabularyTagDao

    companion object : SingletonHolder<WanicchouDatabase, Context>({
        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(WanicchouMigration.MIGRATION_1_2_QUERY)
            }
        }
        Room.databaseBuilder<WanicchouDatabase>(it.applicationContext,
                                                        WanicchouDatabase::class.java,
                                                       "WanicchouDatabase")
                .addMigrations(MIGRATION_1_2)
                .build()
    })
}
