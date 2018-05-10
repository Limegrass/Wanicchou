package util.anki;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Originally from 'ankidroid/apisample'
 * Modified from original.
 */

final public class AnkiDroidConfig {

    // Name of deck which will be created in AnkiDroid
    public static final String DECK_NAME = "J-J Learner's Dictionary";
    // Name of model which will be created in AnkiDroid
    public static final String MODEL_NAME = "jjld";

    // Optional space separated list of tags to add to every note
    public static final Set<String> TAGS = new HashSet<>(Collections.singletonList("JJLD"));
    // List of field names that will be used in AnkiDroid model
    //Many fields will be unused at first
    public static final String[] FIELDS =
            {
                    "Word", "Reading", "Definition", "Furigana", "Pitch", "Notes", "Context", "Dictionary Type"
//                    "Pitch", "Audio", "Grammar",
//                    "Sentence", "SentenceFurigana","SentenceMeaning",
            };

    public static final int FIELDS_INDEX_WORD = 0;
    public static final int FIELDS_INDEX_READING = 1;
    public static final int FIELDS_INDEX_DEFINITION = 2;
    public static final int FIELDS_INDEX_FURIGANA = 3;
    public static final int FIELDS_INDEX_PITCH = 4;
    public static final int FIELDS_INDEX_NOTES  = 5;
    public static final int FIELDS_INDEX_CONTEXT = 6;
    public static final int FIELDS_INDEX_DICTIONARY_TYPE = 7;



    // List of card names that will be used in AnkiDroid (one for each direction of learning)
    public static final String[] CARD_NAMES = {"Word > Reading", "Word > Definition"};

    // CSS to share between all the cards (optional). User will need to install the NotoSans font by themselves
    public static final String CSS = ".card {\n" +
            " font-family: NotoSansJP;\n" +
            " font-size: 24px;\n" +
            " text-align: center;\n" +
            " color: white;\n" +
            " background-color: black;\n" +
            " word-wrap: break-word;\n" +
            "}\n" +
            "@font-face { font-family: \"NotoSansJP\"; src: url('_NotoSansJP-Regular.otf'); }\n" +
            "@font-face { font-family: \"NotoSansJP\"; src: url('_NotoSansJP-Bold.otf'); font-weight: bold; }\n" +
            "\n" +
            ".big { font-size: 24px; }\n" +
            ".small { font-size: 12px;}\n" +
            ".highlight{ color: cyan }\n" +
            "ruby rt { visibility: hidden; }\n" +
            "ruby:hover rt { visibility: visible; }\n";
    // Template for the question of each card
//    static final String QFMT1 = "<div class=big>Reading of: {{Kanji}}</div><br><br>{{Sentence}}";
    //TODO: Add a clozed  type option where they can edit the clozed word through UI
    static final String QFMT1 = "<div class=\"big highlight\">{{Word}}:読み方</div>";
    static final String QFMT2 = "<div class=big>{{furigana:Furigana}}:意味</div>";
    public static final String[] QFMT = {QFMT1, QFMT2};
    // Template for the answer (use identical for both sides)
    static final String AFMT1 = "{{FrontSide}}\n<br>\n<hr id=answer>\n<br>\n{{Reading}}\n<br>\n" +
            "<div class=extra>{{Definition}}";
    static final String AFMT2 = "{{FrontSide}}\n<br>\n<hr id=answer>\n<br>\n" +
            "{{Definition}}";
    public static final String[] AFMT = {AFMT1, AFMT2};

    // Define two keys which will be used when using legacy ACTION_SEND intent
    public static final String FRONT_SIDE_KEY = FIELDS[0];  //Kanji
    public static final String BACK_SIDE_KEY = FIELDS[2];   //Definition

}
