package room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object WanicchouMigrationV2V3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Rename old tables
        database.execSQL(RENAME_BITMASKED_BITMASKED_VOCABULARY_RELATION)
        database.execSQL(RENAME_BITMASKED_DICTIONARY_MATCH_TYPE)
        database.execSQL(RENAME_BITMASKED_MATCH_TYPE)
        // Create replacement tables
        database.execSQL(CREATE_REPLACEMENT_MATCH_TYPE)
        database.execSQL(CREATE_REPLACEMENT_DICTIONARY_MATCH_TYPE)
        database.execSQL(CREATE_REPLACEMENT_VOCABULARY_RELATION)
        // Remove old indices and add new ones
        database.execSQL(DROP_VOCABULARY_RELATION_INDEX_RESULT_VOCABULARY_ID)
        database.execSQL(DROP_VOCABULARY_RELATION_INDEX_SEARCH_VOCABULARY_ID)
        database.execSQL(CREATE_VOCABULARY_RELATION_INDEX_MATCH_TYPE_ID)
        database.execSQL(CREATE_VOCABULARY_RELATION_INDEX_RESULT_VOCABULARY_ID)
        database.execSQL(CREATE_VOCABULARY_RELATION_INDEX_SEARCH_VOCABULARY_ID)
        database.execSQL(CREATE_VOCABULARY_RELATION_INDEX_UNIQUE_COMBINED)
        database.execSQL(DROP_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_ID)
        database.execSQL(DROP_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_MATCH_TYPE_ID)
        database.execSQL(CREATE_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_MATCH_TYPE_ID)
        database.execSQL(CREATE_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_ID)
        database.execSQL(CREATE_DICTIONARY_MATCH_TYPE_INDEX_MATCH_TYPE_ID)
        database.execSQL(CREATE_DICTIONARY_MATCH_TYPE_INDEX_COMBINED)
        // Import all the old data
        database.execSQL(INSERT_NEW_MATCH_TYPES)
        database.execSQL(INSERT_DICTIONARY_MATCH_TYPE_BITMASK_1)
        database.execSQL(INSERT_DICTIONARY_MATCH_TYPE_BITMASK_2)
        database.execSQL(INSERT_DICTIONARY_MATCH_TYPE_BITMASK_3)
        database.execSQL(INSERT_DICTIONARY_MATCH_TYPE_BITMASK_4)
        database.execSQL(INSERT_DICTIONARY_MATCH_TYPE_BITMASK_5)
        database.execSQL(INSERT_DICTIONARY_MATCH_TYPE_BITMASK_6)
        database.execSQL(INSERT_DICTIONARY_MATCH_TYPE_BITMASK_7)
        database.execSQL(INSERT_VOCABULARY_RELATION_1)
        database.execSQL(INSERT_VOCABULARY_RELATION_2)
        database.execSQL(INSERT_VOCABULARY_RELATION_3)
        database.execSQL(INSERT_VOCABULARY_RELATION_4)
        database.execSQL(INSERT_VOCABULARY_RELATION_5)
        database.execSQL(INSERT_VOCABULARY_RELATION_6)
        database.execSQL(INSERT_VOCABULARY_RELATION_7)
        // Cleanup old tables
        database.execSQL(DROP_BITMASKED_VOCABULARY_RELATION)
        database.execSQL(DROP_BITMASKED_DICTIONARY_MATCH_TYPE)
        database.execSQL(DROP_BITMASKED_MATCH_TYPE)
        // Fix a mistake
        database.execSQL(UPDATE_SANSEIDO_DICTIONARY_MATCH_TYPE)
    }

    private const val RENAME_BITMASKED_MATCH_TYPE =
            "ALTER TABLE MatchType RENAME TO BitmaskedMatchType;"

    private const val RENAME_BITMASKED_DICTIONARY_MATCH_TYPE =
            "ALTER TABLE DictionaryMatchType RENAME TO BitmaskedDictionaryMatchType;"

    private const val RENAME_BITMASKED_BITMASKED_VOCABULARY_RELATION =
            "ALTER TABLE VocabularyRelation RENAME TO BitmaskedVocabularyRelation;"

    private const val CREATE_REPLACEMENT_MATCH_TYPE = """
            CREATE TABLE MatchType
            (
                MatchTypeID INTEGER PRIMARY KEY NOT NULL,
                MatchTypeName TEXT NOT NULL,
                TemplateString TEXT NOT NULL
            );"""

    private const val CREATE_REPLACEMENT_DICTIONARY_MATCH_TYPE = """
            CREATE TABLE DictionaryMatchType
            (
                DictionaryMatchTypeID INTEGER PRIMARY KEY NOT NULL,
                DictionaryID INTEGER NOT NULL,
                MatchTypeID INTEGER NOT NULL,
                FOREIGN KEY(DictionaryID) REFERENCES Dictionary(DictionaryID),
                FOREIGN KEY(MatchTypeID) REFERENCES MatchType(MatchTypeID),
                UNIQUE(DictionaryID, MatchTypeID)
            );"""

    private const val CREATE_REPLACEMENT_VOCABULARY_RELATION = """
            CREATE TABLE VocabularyRelation
            (
                VocabularyRelationID INTEGER PRIMARY KEY NOT NULL,
                MatchTypeID INTEGER NOT NULL,
                SearchVocabularyID INTEGER NOT NULL,
                ResultVocabularyID INTEGER NOT NULL,
                FOREIGN KEY(SearchVocabularyID) REFERENCES Vocabulary(VocabularyID),
                FOREIGN KEY(ResultVocabularyID) REFERENCES Vocabulary(VocabularyID),
                FOREIGN KEY(MatchTypeID) REFERENCES MatchType(MatchTypeID),
                UNIQUE(SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            );"""

    private const val DROP_VOCABULARY_RELATION_INDEX_SEARCH_VOCABULARY_ID =
            "DROP INDEX index_VocabularyRelation_SearchVocabularyID;"
    private const val DROP_VOCABULARY_RELATION_INDEX_RESULT_VOCABULARY_ID =
            "DROP INDEX index_VocabularyRelation_ResultVocabularyID;"

    private const val CREATE_VOCABULARY_RELATION_INDEX_SEARCH_VOCABULARY_ID = """
            CREATE INDEX index_VocabularyRelation_SearchVocabularyID 
            ON VocabularyRelation(SearchVocabularyID);"""
    private const val CREATE_VOCABULARY_RELATION_INDEX_MATCH_TYPE_ID = """
            CREATE INDEX index_VocabularyRelation_MatchTypeID
            ON VocabularyRelation(MatchTypeID);"""
    private const val CREATE_VOCABULARY_RELATION_INDEX_RESULT_VOCABULARY_ID = """
            CREATE INDEX index_VocabularyRelation_ResultVocabularyID
            ON VocabularyRelation(ResultVocabularyID);"""
    private const val CREATE_VOCABULARY_RELATION_INDEX_UNIQUE_COMBINED = """
            CREATE UNIQUE INDEX index_VocabularyRelation_SearchVocabularyID_ResultVocabularyID_MatchTypeID
            ON VocabularyRelation(SearchVocabularyID, ResultVocabularyID, MatchTypeID);"""

    private const val INSERT_NEW_MATCH_TYPES = """
            INSERT INTO MatchType
            VALUES
                (1, 'WORD_EQUALS', '%s'),
                (2, 'WORD_STARTS_WITH', '%s%%'),
                (3, 'WORD_ENDS_WITH', '%%%s'),
                (4, 'WORD_CONTAINS', '%%%s%%'),
                (5, 'WORD_WILDCARDS', '%s'),
                (6, 'DEFINITION_CONTAINS', '%%%s%%'),
                (7, 'WORD_OR_DEFINITION_CONTAINS', '%%%s%%');"""

    private const val INSERT_DICTIONARY_MATCH_TYPE_BITMASK_1 = """
            INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
            SELECT bdmt.DictionaryID, 1
            FROM BitmaskedDictionaryMatchType bdmt
            WHERE MatchTypeBitmask & 1 = 1;"""

    private const val INSERT_DICTIONARY_MATCH_TYPE_BITMASK_2 = """
            INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
            SELECT bdmt.DictionaryID, 2
            FROM BitmaskedDictionaryMatchType bdmt
            WHERE MatchTypeBitmask & 2 = 2;"""

    private const val INSERT_DICTIONARY_MATCH_TYPE_BITMASK_3 = """
            INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
            SELECT bdmt.DictionaryID, 3
            FROM BitmaskedDictionaryMatchType bdmt
            WHERE MatchTypeBitmask & 4 = 4;"""

    private const val INSERT_DICTIONARY_MATCH_TYPE_BITMASK_4 = """
            INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
            SELECT bdmt.DictionaryID, 4
            FROM BitmaskedDictionaryMatchType bdmt
            WHERE MatchTypeBitmask & 8 = 8;"""

    private const val INSERT_DICTIONARY_MATCH_TYPE_BITMASK_5 = """
            INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
            SELECT bdmt.DictionaryID, 5
            FROM BitmaskedDictionaryMatchType bdmt
            WHERE MatchTypeBitmask & 16 = 16;"""

    private const val INSERT_DICTIONARY_MATCH_TYPE_BITMASK_6 = """
            INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
            SELECT bdmt.DictionaryID, 6
            FROM BitmaskedDictionaryMatchType bdmt
            WHERE MatchTypeBitmask & 32 = 32;"""

    private const val INSERT_DICTIONARY_MATCH_TYPE_BITMASK_7 = """
            INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
            SELECT bdmt.DictionaryID, 7
            FROM BitmaskedDictionaryMatchType bdmt
            WHERE MatchTypeBitmask & 64 = 64;"""

    private const val INSERT_VOCABULARY_RELATION_1 = """
            INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            SELECT SearchVocabularyID, ResultVocabularyID, 1
            FROM BitmaskedVocabularyRelation
            WHERE MatchTypeBitmask & 1 = 1;"""

    private const val INSERT_VOCABULARY_RELATION_2 = """
            INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            SELECT SearchVocabularyID, ResultVocabularyID, 2
            FROM BitmaskedVocabularyRelation
            WHERE MatchTypeBitmask & 2 = 2;"""

    private const val INSERT_VOCABULARY_RELATION_3 = """
            INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            SELECT SearchVocabularyID, ResultVocabularyID, 3
            FROM BitmaskedVocabularyRelation
            WHERE MatchTypeBitmask & 4 = 4;"""

    private const val INSERT_VOCABULARY_RELATION_4 = """
            INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            SELECT SearchVocabularyID, ResultVocabularyID, 4
            FROM BitmaskedVocabularyRelation
            WHERE MatchTypeBitmask & 8 = 8;"""

    private const val INSERT_VOCABULARY_RELATION_5 = """
            INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            SELECT SearchVocabularyID, ResultVocabularyID, 5
            FROM BitmaskedVocabularyRelation
            WHERE MatchTypeBitmask & 16 = 16;"""

    private const val INSERT_VOCABULARY_RELATION_6 = """
            INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            SELECT SearchVocabularyID, ResultVocabularyID, 6
            FROM BitmaskedVocabularyRelation
            WHERE MatchTypeBitmask & 32 = 32;"""

    private const val INSERT_VOCABULARY_RELATION_7 = """
            INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
            SELECT SearchVocabularyID, ResultVocabularyID, 7
            FROM BitmaskedVocabularyRelation
            WHERE MatchTypeBitmask & 64 = 64;"""

    private const val DROP_BITMASKED_VOCABULARY_RELATION =
            "DROP TABLE BitmaskedVocabularyRelation;"
    private const val DROP_BITMASKED_DICTIONARY_MATCH_TYPE =
            "DROP TABLE BitmaskedDictionaryMatchType;"
    private const val DROP_BITMASKED_MATCH_TYPE = "DROP TABLE BitmaskedMatchType;"

    private const val DROP_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_MATCH_TYPE_ID =
            "DROP INDEX index_DictionaryMatchType_DictionaryMatchTypeID;"
    private const val DROP_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_ID =
            "DROP INDEX index_DictionaryMatchType_DictionaryID;"

    private const val CREATE_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_MATCH_TYPE_ID = """
            CREATE INDEX index_DictionaryMatchType_DictionaryMatchTypeID 
            ON DictionaryMatchType(DictionaryMatchTypeID);"""
    private const val CREATE_DICTIONARY_MATCH_TYPE_INDEX_MATCH_TYPE_ID = """
            CREATE INDEX index_DictionaryMatchType_MatchTypeID 
            ON DictionaryMatchType(MatchTypeID);"""
    private const val CREATE_DICTIONARY_MATCH_TYPE_INDEX_DICTIONARY_ID = """
            CREATE INDEX index_DictionaryMatchType_DictionaryID 
            ON DictionaryMatchType(DictionaryID);"""
    private const val CREATE_DICTIONARY_MATCH_TYPE_INDEX_COMBINED = """
            CREATE UNIQUE INDEX index_DictionaryMatchType_DictionaryID_MatchTypeID 
            ON DictionaryMatchType(DictionaryID, MatchTypeID);"""

    private const val UPDATE_SANSEIDO_DICTIONARY_MATCH_TYPE = """
            UPDATE DictionaryMatchType
            SET MatchTypeID = 7
            WHERE MatchTypeID = 6 AND DictionaryID = 1"""
}
