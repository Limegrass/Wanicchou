package data.arch.anki

import java.util.*

/**
 * Originally from 'ankidroid/apisample'
 * Modified from original.
 */

object AnkiDroidConfig {

    // Name of deck which will be created in AnkiDroid
    const val DECK_NAME = "Wanicchou"
    // Name of model which will be created in AnkiDroid
    const val MODEL_NAME = "wanicchou"

    // Optional space separated list of tags to add to every note
    val TAGS: Set<String> = HashSet<String>(listOf("Wanicchou"))

    // List of field names that will be used in AnkiDroid model
    //Many fields will be unused at first
    val FIELDS = arrayOf("Word",
                        "Word Language",
                        "Definition",
                        "Definition Language",
                        "Dictionary",
                        "Pronunciation",
                        "Pitch",
                        "Furigana",
                        "Notes")
                        // "Pitch",
                        // "Audio",
                        // "Grammar",
                        // "Sentence",
                        // "SentenceFurigana",
                        // "SentenceMeaning",

    const val FIELDS_INDEX_WORD = 0
    const val FIELDS_INDEX_WORD_LANGUAGE = 1
    const val FIELDS_INDEX_DEFINITION = 2
    const val FIELDS_INDEX_DEFINITION_LANGUAGE = 3
    const val FIELDS_INDEX_DICTIONARY = 4
    const val FIELDS_INDEX_PRONUNCIATION = 5
    const val FIELDS_INDEX_PITCH = 6
    const val FIELDS_INDEX_FURIGANA = 7
    const val FIELDS_INDEX_NOTES = 8


    // List of card names that will be used in AnkiDroid (one for each direction of learning)
    val CARD_NAMES = arrayOf("Word > Pronunciation", "Word > Definition")

    //TODO: Rework the format because it only applies to Japanese.
    // Or just keep it until another language is added?
    // CSS to share between all the cards (optional). User will need to install the NotoSans font by themselves
    const val CSS = """
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
}"""
    // Template for the question of each card
    //    static final String QFMT1 = "<div class=big>Reading of: {{Kanji}}</div><br><br>{{Sentence}}";
    //TODO: Add a clozed  type option where they can edit the clozed word through UI
    private const val QFMT1 = "<div class=\"big highlight\">{{Word}}:読み方</div>"
    private const val QFMT2 = "<div class=big>{{furigana:Furigana}}:意味</div>"
    val QFMT = arrayOf(QFMT1, QFMT2)
    // Template for the answer (use identical for both sides)
    private const val AFMT1 = "{{FrontSide}}\n<br>\n<hr id=answer>\n<br>\n{{Pronunciation}}\n<br>\n" + "<div class=extra>{{Definition}}"
    private const val AFMT2 = "{{FrontSide}}\n<br>\n<hr id=answer>\n<br>\n" + "{{Definition}}"
    val AFMT = arrayOf(AFMT1, AFMT2)

    // Define two keys which will be used when using legacy ACTION_SEND intent
    val FRONT_SIDE_KEY = FIELDS[FIELDS_INDEX_WORD]  //Kanji
    val BACK_SIDE_KEY = FIELDS[FIELDS_INDEX_DEFINITION]   //Definition
}
