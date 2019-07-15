package data.room.database


object WanicchouMigration {
    const val MIGRATION_1_2_QUERY =
"""
CREATE TABLE Language
(
    LanguageID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    LanguageCode VARCHAR(2) NOT NULL,
    LanguageName NVARCHAR(322) NOT NULL
);

INSERT INTO Language
(
    LanguageID,
    LanguageCode,
    LanguageName
)
VALUES  (1, 'jp', '日本語'),
        (2, 'en', 'English');

CREATE TABLE Dictionary
(
    DictionaryID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
    DictionaryName NVARCHAR(322) NOT NULL
);

INSERT INTO Dictionary ( DictionaryName )
VALUES ('三省堂');


CREATE TABLE MatchType
(
    MatchTypeBitmask INTEGER NOT NULL PRIMARY KEY,
    MatchTypeName VARCHAR(322) NOT NULL,
    TemplateString VARCHAR(420) NOT NULL
);

INSERT INTO MatchType (MatchTypeName, TemplateString, MatchTypeBitmask)
VALUES  ('WORD_EQUALS', "%s", 1),
        ('WORD_STARTS_WITH' , "%s%%", 2),
        ('WORD_ENDS_WITH' , "%%%s", 4),
        ('WORD_CONTAINS' , "%%%s%%", 8),
        ('WORD_WILDCARDS' , "%s", 16),
        ('DEFINITION_CONTAINS' , "%%%s%%", 32),
        ('WORD_OR_DEFINITION_CONTAINS' , "%%%s%%", 64);

CREATE TABLE DictionaryMatchType
(
    DictionaryMatchTypeID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    DictionaryID INT NOT NULL,
    MatchTypeBitmask INT NOT NULL,
    FOREIGN KEY(DictionaryID) REFERENCES Dictionary(DictionaryID)
    FOREIGN KEY(MatchTypeBitmask) REFERENCES MatchType(MatchTypeBitmask)
);

INSERT INTO DictionaryMatchType (DictionaryMatchTypeID, DictionaryID, MatchTypeBitmask)
VALUES  (1, 1, 1),
        (2, 1, 2),
        (3, 1, 4),
        (4, 1, 8),
        (5, 1, 32);

CREATE TABLE Translation
(
    TranslationID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    SourceLanguageID INT NOT NULL,
    TargetLanguageID INT NOT NULL,
    TranslationName NVARCHAR(100) NOT NULL,
    DictionaryID INT NOT NULL
    FOREIGN KEY(DictionaryID) REFERENCES Dictionary(DictionaryID)
    FOREIGN KEY(SourceLanguageID) REFERENCES Language(LanguageID)
    FOREIGN KEY(TargetLanguageID) REFERENCES Language(LanguageID)
);

INSERT Translation (SourceLanguageID, TargetLanguageID, TranslationName, DictionaryID)
VALUES  (1, 1, '国語', 1),
        (1, 2, '和英', 1),
        (2, 1, '英和', 1);


CREATE TABLE Vocabulary
(
    VocabularyID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    Word VARCHAR(420) NOT NULL,
    Pronunciation VARCHAR(420) NOT NULL,
    Pitch VARCHAR(4) NOT NULL,
    LanguageID INT NOT NULL,
    FOREIGN KEY LanguageID REFERENCES Language(LanguageID)
);

CREATE UNIQUE INDEX VocabularyIndex ON Vocabulary(Word, Pronunciation, LanguageID, Pitch)

CREATE TABLE Definition
(
    DefinitionID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    VocabularyID INT NOT NULL,
    DictionaryID INT NOT NULL,
    DefinitionText NVARCHAR(MAX) NOT NULL,
    LanguageID INT NOT NULL,
    FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID),
    FOREIGN KEY(DictionaryID) REFERENCES Dictionary(DictionaryID)
    FOREIGN KEY(LanguageID) REFERENCES TableName(ForeignKeyColumn)
);

CREATE TABLE Tag
(
    TagID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    TagText NVARCHAR(100) NOT NULL
);

CREATE TABLE VocabularyTag
(
    VocabularyTagID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    VocabularyID INT NOT NULL,
    TagID INT NOT NULL,
    FOREIGN KEY(TagID) REFERENCES Tag(TagID),
    FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID)
);
CREATE UNIQUE INDEX VocabularyTagIndex ON VocabularyTag(TagID, VocabularyID)

CREATE TABLE VocabularyRelation
(
    VocabularyRelationID INT NOT NULL PRIMARY KEY AUTOINCREMENT,
    SearchVocabularyID INT NOT NULL,
    ResultVocabularyID INT NOT NULL,
    FOREIGN KEY(SearchVocabularyID) REFERENCES Vocabulary(VocabularyID),
    FOREIGN KEY(ResultVocabularyID) REFERENCES Vocabulary(VocabularyID)
);
CREATE UNIQUE INDEX VocabularyRelationIndex ON VocabularyRelation(SearchVocabularyID, ResultVocabularyID)

CREATE TABLE VocabularyNote
(
    VocabularyNoteID INT NOT NULL PRIMARY KEY,
    VocabularyID INT NOT NULL,
    NoteText NVARCHAR(MAX),
    FOREIGN KEY(VocabularyID) REFERENCES Vocabulary(VocabularyID)
);

CREATE TABLE DefinitionNote
(
    DefinitionNoteID INT NOT NULL PRIMARY KEY,
    DefinitionID INT NOT NULL,
    NoteText NVARCHAR(MAX) NOT NULL
    FOREIGN KEY(DefinitionID) REFERENCES Definition(DefinitionID)
);

INSERT INTO Vocabulary
(
    Word,
    Pronunciation,
    Pitch,
    LanguageID
)
SELECT vo.Word,
       vo.Reading,
       vo.Pitch,
    CASE WHEN SUBSTR(vo.DictionaryType, 1, 1) = 'J' THEN 1
        ELSE 2 END
FROM VocabularyWords vo;

INSERT INTO Definition
(
    VocabularyID,
    DictionaryID,
    DefinitionText,
    LanguageID
)
SELECT vo.VocabularyID,
       1,
       vo.Definition,
    CASE WHEN SUBSTR(vo.DictionaryType, 1, 1) = 'J' THEN 1
        ELSE 2 END
FROM VocabularyWords vo;

INSERT INTO VocabularyRelation
(
    SearchVocabularyID,
    ResultVocabularyID
)
SELECT rw.RelatedWordID,
       rw.FKBaseWordID
FROM RelatedWords rw;

INSERT INTO VocabularyNote(VocabularyID, NoteText)
SELECT vo.VocabularyID,
       n.Note
FROM Notes n
JOIN VocabularyWords vo
    ON vo.Word = n.Word;

INSERT INTO VocabularyNote (VocabularyID, NoteText)
SELECT vo.VocabularyID,
       c.Context
FROM WordContext c
JOIN VocabularyWords vo
    ON vo.Word = n.Word;

DROP TABLE Notes;
DROP TABLE VocabularyWords;
DROP TABLE WordContext;
DROP TABLE RelatedWords;
"""

    const val MIGRATION_2_3_QUERY =
"""
-- Rename the old tables and so they can be replaced.
ALTER TABLE MatchType
RENAME TO BitmaskedMatchType;

ALTER TABLE DictionaryMatchType
RENAME TO BitmaskedDictionaryMatchType;

ALTER TABLE VocabularyRelation
RENAME TO BitmaskedVocabularyRelation;

-- Create their replacements. Simple since the bitmasks weren't foreign keys
CREATE TABLE MatchType
(
    MatchTypeID INTEGER PRIMARY KEY NOT NULL,
    MatchTypeName TEXT NOT NULL,
    TemplateString TEXT NOT NULL
);
CREATE TABLE DictionaryMatchType
(
    DictionaryMatchTypeID INTEGER PRIMARY KEY NOT NULL,
    FOREIGN KEY(DictionaryID)  REFERENCES Dictionary(DictionaryID),
    FOREIGN KEY(MatchTypeID) REFERENCES MatchType(MatchTypeID),
    UNIQUE(DictionaryID, MatchTypeID)
);

CREATE TABLE VocabularyRelation
(
    VocabularyRelationID INTEGER PRIMARY KEY NOT NULL,
    FOREIGN KEY(SearchVocabularyID) REFERENCES Vocabulary(VocabularyID),
    FOREIGN KEY(ResultVocabularyID) REFERENCES Vocabulary(VocabularyID),
    FOREIGN KEY(MatchTypeID) REFERENCES MatchType(MatchTypeID),
    UNIQUE(SearchVocabularyID, ResultVocabularyID, MatchTypeID)
);

-- Insert Values
INSERT INTO MatchType
VALUES
    (1, 'WORD_EQUALS', '%s'),
    (2, 'WORD_STARTS_WITH', '%s%%'),
    (3, 'WORD_ENDS_WITH', '%%%s'),
    (4, 'WORD_CONTAINS', '%%%s%%'),
    (5, 'WORD_WILDCARDS', '%s'),
    (6, 'DEFINITION_CONTAINS', '%%%s%%'),
    (7, 'WORD_OR_DEFINITION_CONTAINS', '%%%s%%');

INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
SELECT bdmt.DictionaryID, 1
FROM BitmaskedDictionaryMatchType bdmt
WHERE MatchTypeBitmask & 1 = 1;

INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
SELECT bdmt.DictionaryID, 2
FROM BitmaskedDictionaryMatchType bdmt
WHERE MatchTypeBitmask & 2 = 2;

INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
SELECT bdmt.DictionaryID, 3
FROM BitmaskedDictionaryMatchType bdmt
WHERE MatchTypeBitmask & 4 = 4;

INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
SELECT bdmt.DictionaryID, 4
FROM BitmaskedDictionaryMatchType bdmt
WHERE MatchTypeBitmask & 8 = 8;

INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
SELECT bdmt.DictionaryID, 5
FROM BitmaskedDictionaryMatchType bdmt
WHERE MatchTypeBitmask & 16 = 16;

INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
SELECT bdmt.DictionaryID, 6
FROM BitmaskedDictionaryMatchType bdmt
WHERE MatchTypeBitmask & 32 = 32;

INSERT INTO DictionaryMatchType (DictionaryID, MatchTypeID)
SELECT bdmt.DictionaryID, 7
FROM BitmaskedDictionaryMatchType bdmt
WHERE MatchTypeBitmask & 64 = 64;


INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
SELECT SearchVocabularyID, ResultVocabularyID, 1
FROM BitmaskedVocabularyRelation
WHERE MatchTypeBitmask & 1 = 1;

INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
SELECT SearchVocabularyID, ResultVocabularyID, 2
FROM BitmaskedVocabularyRelation
WHERE MatchTypeBitmask & 2 = 2;

INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
SELECT SearchVocabularyID, ResultVocabularyID, 3
FROM BitmaskedVocabularyRelation
WHERE MatchTypeBitmask & 4 = 4;

INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
SELECT SearchVocabularyID, ResultVocabularyID, 4
FROM BitmaskedVocabularyRelation
WHERE MatchTypeBitmask & 8 = 8;

INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
SELECT SearchVocabularyID, ResultVocabularyID, 5
FROM BitmaskedVocabularyRelation
WHERE MatchTypeBitmask & 16 = 16;

INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
SELECT SearchVocabularyID, ResultVocabularyID, 6
FROM BitmaskedVocabularyRelation
WHERE MatchTypeBitmask & 32 = 32;

INSERT INTO VocabularyRelation (SearchVocabularyID, ResultVocabularyID, MatchTypeID)
SELECT SearchVocabularyID, ResultVocabularyID, 7
FROM BitmaskedVocabularyRelation
WHERE MatchTypeBitmask & 64 = 64;

-- Remove the old tables
DROP TABLE BitmaskedVocabularyRelation;
DROP TABLE BitmaskedMatchType;
DROP TABLE BitmaskedDictionaryMatchType;

UPDATE DictionaryMatchType
SET MatchTypeID = 7
WHERE MatchTypeID = 6 AND DictionaryID = 1
"""
}