package data.room

import android.content.Context
import android.arch.persistence.room.*
import data.core.SingletonHolder
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
        Room.databaseBuilder<WanicchouDatabase>(it.applicationContext,
                WanicchouDatabase::class.java, "WanicchouDatabase")
                .build()
    })
}
