package data.anki

import data.enums.Dictionary
import data.enums.Language
import data.models.Definition
import data.models.Vocabulary

/**
 * Defines the structure of cards imported to AnkiDroid from Wanicchou
 */
object AnkiDroidConfig : IAnkiDroidConfig<WanicchouAnkiEntry> {
    override val frontSideKey: String
        get() = "Word"
    override val backSideKey: String
        get() = "Definition"

    override fun mapFromNoteFields(fields : Array<String>): WanicchouAnkiEntry {
        val vocabularyLanguage = Language.values().single {
            it.languageCode == fields[fieldMapping.getValue(VOCABULARY_LANGUAGE_KEY)]
        }
        val vocabulary = Vocabulary(fields[fieldMapping.getValue(WORD_KEY)],
                fields[fieldMapping.getValue(PRONUNCIATION_KEY)],
                fields[fieldMapping.getValue(PITCH_KEY)],
                vocabularyLanguage)

        val definitionLanguage = Language.values().single {
            it.languageCode == fields[fieldMapping.getValue(DEFINITION_LANGUAGE_KEY)]
        }

        val dictionary = Dictionary.values().single {
            it.dictionaryName == fields[fieldMapping.getValue(DICTIONARY_KEY)]
        }
        val definition = Definition(fields[fieldMapping.getValue(DEFINITION_TEXT_KEY)],
                definitionLanguage,
                dictionary)
        val notes = fields[fieldMapping.getValue(NOTES_KEY)].split(NOTES_DELIMITER)
        return WanicchouAnkiEntry(vocabulary, definition, notes)
    }


    override fun mapToNoteFields(noteEntry: WanicchouAnkiEntry): Array<String> {
        val word = noteEntry.vocabulary.word
        val pronunciation = noteEntry.vocabulary.pronunciation
        val furigana = getFurigana(word, pronunciation)
        val fields = arrayOfNulls<String>(fieldMapping.size)
        fields[fieldMapping.getValue(WORD_KEY)] =                word
        fields[fieldMapping.getValue(VOCABULARY_LANGUAGE_KEY)] = noteEntry.vocabulary.language.languageCode
        fields[fieldMapping.getValue(DEFINITION_TEXT_KEY)] =     noteEntry.definition.definitionText
        fields[fieldMapping.getValue(DEFINITION_LANGUAGE_KEY)] = noteEntry.definition.language.languageCode
        fields[fieldMapping.getValue(DICTIONARY_KEY)] =          noteEntry.definition.dictionary.dictionaryName
        fields[fieldMapping.getValue(PRONUNCIATION_KEY)] =       pronunciation
        fields[fieldMapping.getValue(PITCH_KEY)] =               noteEntry.vocabulary.pitch
        fields[fieldMapping.getValue(FURIGANA_KEY)] =            furigana
        fields[fieldMapping.getValue(NOTES_KEY)] =               noteEntry.notes.joinToString(NOTES_DELIMITER)
        return fields.map{ it!! }.toTypedArray()
    }

    override val sortField: Int?
        get() = null

    override val deckName: String
        get() = "Wanicchou"
    override val modelName: String
        get() = "Wanicchou"

    override val fields: Array<String>
        get() {
            val fields = arrayOfNulls<String>(fieldMapping.size)
            for ((fieldName, index) in fieldMapping){
                fields[index] = fieldName
            }
            return fields.map{ it!! }.toTypedArray()
        }

    private const val NOTES_DELIMITER : String = "\n" + 0x0
    private const val WORD_KEY = "Word"
    private const val VOCABULARY_LANGUAGE_KEY = "Word Language"
    private const val DEFINITION_TEXT_KEY = "Definition"
    private const val DEFINITION_LANGUAGE_KEY = "Definition Language"
    private const val DICTIONARY_KEY = "Dictionary"
    private const val PRONUNCIATION_KEY = "Pronunciation"
    private const val PITCH_KEY = "Pitch"
    private const val FURIGANA_KEY = "Furigana"
    private const val NOTES_KEY = "Notes"

    private fun getFurigana(word: String, pronunciation: String): String {
        return if (word == pronunciation) {
            pronunciation
        } else "$word[$pronunciation]"
    }

    private val fieldMapping : Map<String, Int>
        get() = mapOf(WORD_KEY to 0,
                      VOCABULARY_LANGUAGE_KEY to 1,
                      DEFINITION_TEXT_KEY to 2,
                      DEFINITION_LANGUAGE_KEY to 3,
                      DICTIONARY_KEY to 4,
                      PRONUNCIATION_KEY to 5,
                      PITCH_KEY to 6,
                      FURIGANA_KEY to 7,
                      NOTES_KEY to 8)

    override val cardFormats: List<CardFormat>
        get() = listOf(pronunciationFormat, definitionFormat)

    //TODO: Add a clozed  type option where they can edit the clozed word through UI
    private val pronunciationFormat = CardFormat("Word > Pronunciation",
            """<div class="big highlight">{{Word}}:読み方</div>""",
            """
                {{FrontSide}}
                <br>
                <hr id=answer>
                <br>
                {{Pronunciation}}
                <br>
                <div class=extra>{{Definition}}
            """.trimIndent())
    private val definitionFormat = CardFormat("Word > Definition",
            """<div class=big>{{furigana:Furigana}}:意味</div>""",
            """
                {{FrontSide}}
                <br>
                <hr id=answer>
                <br>
                {{Definition}}
            """.trimIndent())

    override val css: String
        get() = """
                .card {
                    font-family: 'NotoSansJP';
                    font-size: 24px;
                    text-align: center;
                    color: white;
                    background-color: #202020;
                    word-wrap: break-word;
                }

                @font-face {
                    font-family: 'NotoSansJP';
                    src: url('_NotoSansJP-Regular.otf');
                }

                @font-face {
                    font-family: 'NotoSansJP';
                    src: url('_NotoSansJP-Bold.otf');
                    font-weight: bold;
                }

                .big {
                    font-size: 24px;
                }

                .small {
                    font-size: 12px;
                }

                .highlight {
                    color: #247E80
                }

                ruby rt {
                    visibility: hidden;
                }
                ruby:hover rt {
                    visibility: visible;
                }
                """.trimIndent()
}
