package util.anki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
                    "Kanji", "Reading", "Definition", "Furigana", "Notes", "Context"
//                    "Pitch", "Audio", "Grammar",
//                    "Sentence", "SentenceFurigana","SentenceMeaning",
            };

    public static final int FIELDS_INDEX_KANJI = 0;
    public static final int FIELDS_INDEX_READING = 1;
    public static final int FIELDS_INDEX_DEFINITION = 2;
    public static final int FIELDS_INDEX_FURIGANA = 3;
    public static final int FIELDS_INDEX_NOTES  = 4;
    public static final int FIELDS_INDEX_CONTEXT = 5;



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
            ".big { font-size: 48px; }\n" +
            ".small { font-size: 18px;}\n" +
            ".highlight{ color: cyan }\n" +
            "ruby rt { visibility: hidden; }\n" +
            "ruby:hover rt { visibility: visible; }\n";
    // Template for the question of each card
//    static final String QFMT1 = "<div class=big>Reading of: {{Kanji}}</div><br><br>{{Sentence}}";
    //TODO: Add a clozed  type option where they can edit the clozed word through UI
    static final String QFMT1 = "<div class=big><div class=highlight>{{Kanji}}</div>:読み方</div>";
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

    /**
     * Generate the ArrayList<HashMap> example data which will be sent to AnkiDroid
     */
    public static List<Map<String, String>> getExampleData() {
//        "Kanji", "Reading", "Definition", "Furigana", "Notes", "Context"
//        final String[] EXAMPLE_WORDS = {"例", "データ", "送る"};
        final String[] EXAMPLE_KANJIS = {"例", "データ", "送る"};
        final String[] EXAMPLE_READINGS = {"れい", "データ", "おくる"};
        final String[] EXAMPLE_DEFINITION = {"Example", "Data", "To send"};
//        final String[] EXAMPLE_TRANSLATIONS = {"Example", "Data", "To send"};
        final String[] EXAMPLE_FURIGANA = {"例[れい]", "データ", "送[おく]る"};
        final String[] EXAMPLE_NOTES = {"test1", "test2", "btw"};
        final String[] EXAMPLE_CONTEXT = {"anime", "bakatest", "imoutosaeireba"};
//        final String[] EXAMPLE_GRAMMAR = {"P, adj-no, n, n-pref", "P, n", "P, v5r, vt"};
//        final String[] EXAMPLE_SENTENCE = {"そんな先例はない。", "きゃ～データが消えた！", "放蕩生活を送る。"};
//        final String[] EXAMPLE_SENTENCE_FURIGANA = {"そんな 先例[せんれい]はない。", "きゃ～データが 消[き]えた！",
//                "放蕩[ほうとう] 生活[せいかつ]を 送[おく]る。"};
//        final String[] EXAMPLE_SENTENCE_DEFINITION = {"We have no such example", "Oh, I lost the data！",
//                "I lead a fast way of living."};

        List<Map<String, String>> data = new ArrayList<>();
        for (int idx = 0; idx < EXAMPLE_KANJIS.length; idx++) {
            Map<String, String> hm = new HashMap<>();
            hm.put(FIELDS[0], EXAMPLE_KANJIS[idx]);
            hm.put(FIELDS[1], EXAMPLE_READINGS[idx]);
            hm.put(FIELDS[2], EXAMPLE_DEFINITION[idx]);
            hm.put(FIELDS[3], EXAMPLE_FURIGANA[idx]);
            hm.put(FIELDS[4], EXAMPLE_NOTES[idx]);
            hm.put(FIELDS[5], EXAMPLE_CONTEXT[idx]);
            data.add(hm);
        }
        return data;
    }
}
