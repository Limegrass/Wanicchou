package room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object WanicchouMigrationV2V3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        dropRemovedTables(db)
        redefineVocabulary(db)
        redefineDefinition(db)
        redefineVocabularyTag(db)
        redefineVocabularyNote(db)
        redefineDefinitionNote(db)
        createVocabularyAndTag(db)
    }
    private fun dropRemovedTables(db : SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE DictionaryMatchType")
        db.execSQL("DROP TABLE VocabularyRelation")
        db.execSQL("DROP TABLE MatchType")
        db.execSQL("DROP TABLE Translation")
    }

    //<editor-fold desc="Vocabulary">
    private fun redefineVocabulary(db : SupportSQLiteDatabase){
        db.execSQL(ALTER_TABLE_VOCABULARY_RENAME)
        db.execSQL(CREATE_REPLACEMENT_VOCABULARY)
        db.execSQL(DROP_OLD_UNIQUE_INDEX_VOCABULARY)
        db.execSQL(CREATE_UNIQUE_INDEX_VOCABULARY)
        db.execSQL(COPY_VOCABULARY_DATA)
        db.execSQL(DROP_OLD_VOCABULARY)
    }
    private const val ALTER_TABLE_VOCABULARY_RENAME = """ 
        ALTER TABLE Vocabulary
        RENAME TO VocabularyData
    """
    private const val CREATE_REPLACEMENT_VOCABULARY = """
        CREATE TABLE Vocabulary
        (
            VocabularyID INTEGER PRIMARY KEY NOT NULL,
            Word TEXT NOT NULL,
            Pronunciation TEXT NOT NULL,
            Pitch TEXT NOT NULL,
            LanguageID INTEGER NOT NULL,
            FOREIGN KEY(LanguageID) REFERENCES Language(LanguageID)
                ON DELETE CASCADE
        );"""

    private const val DROP_OLD_UNIQUE_INDEX_VOCABULARY = """
        DROP INDEX index_Vocabulary_Word_Pronunciation_LanguageID_Pitch
    """
    private const val CREATE_UNIQUE_INDEX_VOCABULARY = """
        CREATE UNIQUE INDEX index_Vocabulary_Word_Pronunciation_Pitch_LanguageID
        ON Vocabulary(Word, Pronunciation, Pitch, LanguageID)
    """

    private const val COPY_VOCABULARY_DATA = """
        INSERT INTO Vocabulary(
            VocabularyID,
            Word,
            Pronunciation,
            Pitch,
            LanguageID)
        SELECT 
            v.VocabularyID,
            v.Word,
            v.Pronunciation,
            v.Pitch,
            v.LanguageID
        FROM VocabularyData v
    """
    private const val DROP_OLD_VOCABULARY = """DROP TABLE VocabularyData"""
    //</editor-fold>

    //<editor-fold desc="Definition">
    private fun redefineDefinition(db : SupportSQLiteDatabase){
        db.execSQL(ALTER_TABLE_DEFINITION_RENAME)
        db.execSQL(CREATE_REPLACEMENT_DEFINITION)
        db.execSQL(CREATE_DEFINITION_COMBINED_UNIQUE_INDEX)
        db.execSQL(COPY_DEFINITION_DATA)
        db.execSQL(DROP_OLD_DEFINITION)
    }
    private const val ALTER_TABLE_DEFINITION_RENAME = """ 
        ALTER TABLE Definition
        RENAME TO DefinitionData
    """
    private const val CREATE_REPLACEMENT_DEFINITION = """
        CREATE TABLE Definition
        (
            DefinitionID INTEGER PRIMARY KEY NOT NULL,
            DefinitionText TEXT NOT NULL,
            LanguageID INTEGER NOT NULL,
            DictionaryID INTEGER NOT NULL,
            VocabularyID INTEGER NOT NULL,
            FOREIGN KEY(LanguageID) REFERENCES Language(LanguageID)
                ON DELETE CASCADE,
            FOREIGN KEY(DictionaryID) REFERENCES Dictionary(DictionaryID)
                ON DELETE CASCADE,
            FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID)
                ON DELETE CASCADE
        );"""

    private const val COPY_DEFINITION_DATA = """
        INSERT INTO Definition(
            DefinitionID,
            DefinitionText,
            LanguageID,
            DictionaryID,
            VocabularyID)
        SELECT 
            d.DefinitionID,
            d.DefinitionText,
            d.LanguageID,
            d.DictionaryID,
            d.VocabularyID
        FROM DefinitionData d
    """
    private const val DROP_OLD_DEFINITION = """DROP TABLE DefinitionData"""
    private const val CREATE_DEFINITION_COMBINED_UNIQUE_INDEX = """
        CREATE UNIQUE INDEX index_Definition_VocabularyID_LanguageID_DictionaryID
        ON Definition(VocabularyID, LanguageID, DictionaryID)"""
    //</editor-fold>

    //<editor-fold desc="VocabularyNote">
    private fun redefineVocabularyNote(db : SupportSQLiteDatabase){
        db.execSQL(ALTER_TABLE_VOCABULARY_NOTE_RENAME)
        db.execSQL(CREATE_REPLACEMENT_VOCABULARY_NOTE)
        db.execSQL(COPY_VOCABULARY_NOTE_DATA)
        db.execSQL(DROP_OLD_VOCABULARY_NOTE)
        db.execSQL(CREATE_INDEX_VOCABULARY_NOTE_VOCABULARY_ID)
    }
    private const val ALTER_TABLE_VOCABULARY_NOTE_RENAME = """ 
        ALTER TABLE VocabularyNote
        RENAME TO VocabularyNoteData
    """
    private const val CREATE_REPLACEMENT_VOCABULARY_NOTE = """
        CREATE TABLE VocabularyNote
        (
            VocabularyNoteID INTEGER PRIMARY KEY NOT NULL,
            NoteText TEXT NOT NULL,
            VocabularyID INTEGER NOT NULL,
            FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID)
                ON DELETE CASCADE
        );"""

    private const val COPY_VOCABULARY_NOTE_DATA = """
        INSERT INTO VocabularyNote(
            VocabularyNoteID,
            NoteText,
            VocabularyID)
        SELECT 
            vn.VocabularyNoteID,
            vn.NoteText,
            vn.VocabularyID
        FROM VocabularyNoteData vn
    """
    private const val DROP_OLD_VOCABULARY_NOTE = """DROP TABLE VocabularyNoteData"""
    private const val CREATE_INDEX_VOCABULARY_NOTE_VOCABULARY_ID = """
        CREATE INDEX index_VocabularyNote_VocabularyID
        ON VocabularyNote(VocabularyID)"""
    //</editor-fold>

    //<editor-fold desc="DefinitionNote">
    private fun redefineDefinitionNote(db : SupportSQLiteDatabase){
        db.execSQL(ALTER_TABLE_DEFINITION_NOTE_RENAME)
        db.execSQL(CREATE_REPLACEMENT_DEFINITION_NOTE)
        db.execSQL(COPY_DEFINITION_NOTE_DATA)
        db.execSQL(DROP_OLD_DEFINITION_NOTE)
        db.execSQL(CREATE_INDEX_DEFINITION_NOTE_DEFINITION_ID)
    }
    private const val ALTER_TABLE_DEFINITION_NOTE_RENAME = """ 
        ALTER TABLE DefinitionNote
        RENAME TO DefinitionNoteData
    """
    private const val CREATE_REPLACEMENT_DEFINITION_NOTE = """
        CREATE TABLE DefinitionNote
        (
            DefinitionNoteID INTEGER PRIMARY KEY NOT NULL,
            NoteText TEXT NOT NULL,
            DefinitionID INTEGER NOT NULL,
            FOREIGN KEY(DefinitionID) REFERENCES Definition(DefinitionID)
                ON DELETE CASCADE
        );"""

    private const val COPY_DEFINITION_NOTE_DATA = """
        INSERT INTO DefinitionNote(
            DefinitionNoteID,
            NoteText,
            DefinitionID)
        SELECT 
            dn.DefinitionNoteID,
            dn.NoteText,
            dn.DefinitionID
        FROM DefinitionNoteData dn
    """
    private const val DROP_OLD_DEFINITION_NOTE = """DROP TABLE DefinitionNoteData"""
    private const val CREATE_INDEX_DEFINITION_NOTE_DEFINITION_ID = """
        CREATE INDEX index_DefinitionNote_DefinitionID
        ON DefinitionNote(DefinitionID)"""
    //</editor-fold>

    //<editor-fold desc="VocabularyTag">
    private fun redefineVocabularyTag(db : SupportSQLiteDatabase){
        db.execSQL(ALTER_TABLE_VOCABULARY_TAG_RENAME)
        db.execSQL(CREATE_REPLACEMENT_VOCABULARY_TAG)
        db.execSQL(COPY_VOCABULARY_TAG_DATA)
        db.execSQL(DROP_OLD_VOCABULARY_TAG)
        db.execSQL(CREATE_INDEX_VOCABULARY_TAG_VOCABULARY_ID)
    }
    private const val ALTER_TABLE_VOCABULARY_TAG_RENAME = """ 
            ALTER TABLE VocabularyTag
            RENAME TO VocabularyTagData
        """
    private const val CREATE_REPLACEMENT_VOCABULARY_TAG = """
            CREATE TABLE VocabularyTag
            (
                VocabularyTagID INTEGER PRIMARY KEY NOT NULL,
                TagID INTEGER NOT NULL,
                VocabularyID INTEGER NOT NULL,
                FOREIGN KEY(TagID) REFERENCES Tag(TagID)
                    ON DELETE CASCADE,
                FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID)
                    ON DELETE CASCADE
            );"""

    private const val COPY_VOCABULARY_TAG_DATA = """
            INSERT INTO VocabularyTag(
                VocabularyTagID,
                TagID,
                VocabularyID)
            SELECT 
                vt.VocabularyTagID,
                vt.TagID,
                vt.VocabularyID
            FROM VocabularyTagData vt
        """
    private const val DROP_OLD_VOCABULARY_TAG = """DROP TABLE VocabularyTagData"""
    private const val CREATE_INDEX_VOCABULARY_TAG_VOCABULARY_ID = """
            CREATE UNIQUE INDEX index_VocabularyTag_VocabularyID_TagID
            ON VocabularyTag(VocabularyID, TagID)"""
    //</editor-fold>

    //<editor-fold desc="VocabularyAndTag">
    private fun createVocabularyAndTag(db: SupportSQLiteDatabase){
        db.execSQL(CREATE_VIEW_VOCABULARY_AND_TAB)
    }
    private const val CREATE_VIEW_VOCABULARY_AND_TAB =
"""CREATE VIEW `VocabularyAndTag` AS SELECT v.VocabularyID,
       v.Word,
       v.Pitch,
       v.Pronunciation,
       v.LanguageID,
       t.TagID,
       t.TagText
FROM Vocabulary v
JOIN VocabularyTag vt
    ON v.VocabularyID = vt.VocabularyID
JOIN Tag t
    ON vt.TagID = t.TagID
    """
    //</editor-fold>
}

