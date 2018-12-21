package data.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.content.Context
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
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
        val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                """
BEGIN TRAN
    CREATE TABLE Dictionary
    (
        DictionaryID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        DictionaryName NVARCHAR(322); NOT NULL
    );

    CREATE TABLE Vocabulary
    (
        VocabularyID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        Word VARCHAR(420), NOT NULL,
        Pronunciation VARCHAR(420), NOT NULL,
        Pitch VARCHAR(4), NOT NULL,
        LanguageCode VARCHAR(2), NOT NULL
    );

    CREATE TABLE Definition
    (
        DefinitionID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        VocabularyID INT NOT NULL,
        DictionaryID INT NOT NULL,
        DefinitionText NVARCHAR(MAX); NOT NULL,
        LanguageCode VARCHAR(2); NOT NULL,
        FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID);
        FOREIGN KEY(DictionaryID) REFERENCES Dictionary(DictionaryID)
    );

    CREATE TABLE Tag
    (
        TagID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        TagText NVARCHAR(100); NOT NULL
    );

    CREATE TABLE VocabularyTag
    (
        VocabularyTagID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        VocabularyID INT NOT NULL,
        TagID INT NOT NULL,
        FOREIGN KEY(TagID); REFERENCES Tag(TagID);,
        FOREIGN KEY(VocabularyID); REFERENCES Vocabulary(VocabularyID);
    );

    CREATE TABLE VocabularyRelation
    (
        VocabularyRelationID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        SearchVocabularyID INT NOT NULL,
        ResultVocabularyID INT NOT NULL,
        FOREIGN KEY(SearchVocabularyID) REFERENCES Vocabulary(VocabularyID);,
        FOREIGN KEY(ResultVocabularyID) REFERENCES Vocabulary(VocabularyID);
    );

    CREATE TABLE VocabularyNote
    (
        VocabularyNoteID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        VocabularyID INT NOT NULL,
        NoteText NVARCHAR(MAX);,
        FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID);
    );

    CREATE TABLE DefinitionNote
    (
        DefinitionNoteID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
        DefinitionID INT NOT NULL,
        NoteText NVARCHAR(MAX); NOT NULL
        FOREIGN KEY(DefinitionID) REFERENCES Definition(DefinitionID);
    );

    INSERT INTO Dictionary ( DictionaryName )
    VALUES ('Sanseido')

    INSERT INTO Vocabulary
    (
        Word,
        Pronunciation,
        Pitch,
        LanguageCode
    )
    SELECT vo.Word,
        vo.Reading,
        vo.Pitch,
        CASE WHEN SUBSTR(vo.DictionaryType, 1, 1) = 'J' THEN 'jp'
            ELSE 'en' END
    FROM VocabularyWords vo;

    INSERT INTO Definition
    (
        VocabularyID,
        DictionaryID,
        DefinitionText,
        LanguageCode
    )
    SELECT vo.VocabularyID,
        1,
        vo.Definition,
        CASE WHEN SUBSTR(vo.DictionaryType, 2, 2) = 'J' THEN 'jp'
            ELSE 'en' END
    FROM VocabularyWords vo;

    INSERT INTO VocabularyRelation
    (
        SearchVocabularyID,
        ResultVocabularyID
    )
    SELECT rw.RelatedWordID,
        rw.FKBaseWordID,
    FROM RelatedWords rw;

    INSERT INTO VocabularyNote (VocabularyID, NoteText)
    SELECT vo.VocabularyID, n.Note
    FROM Notes n
    JOIN VocabularyWords vo
        ON vo.Word = n.Word;

    INSERT INTO VocabularyNote (VocabularyID, NoteText)
    SELECT vo.VocabularyID, c.Context
    FROM WordContext c
    JOIN VocabularyWords vo
        ON vo.Word = n.Word;

    DROP TABLE Notes;
    DROP TABLE VocabularyWords;
    DROP TABLE WordContext;
    DROP TABLE RelatedWords;
COMMIT TRAN
                """.trimIndent()
            }
        }
        Room.databaseBuilder<WanicchouDatabase>(it.applicationContext,
                WanicchouDatabase::class.java, "WanicchouDatabase")
                .addMigrations(MIGRATION_1_2)
                .build()
    })
}
