package data;

import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Limegrass on 3/19/2018.
 */

public class SanseidoSearch {

    private final static String SANSEIDOU_BASE_URL = "https://www.sanseido.biz/User/Dic/Index.aspx";
    private final static String PARAM_WORD_QUERY = "TWords";

    // Order of dictionaries under select dictionaries
    // First is the one that displays
    private final static String PARAM_DORDER= "DORDER";
    private final static String DORDER_JJ = "15";
    private final static String DORDER_EJ = "16";
    private final static String DORDER_JE = "17";
    private final static String DORDER_DEFAULT = DORDER_JJ + DORDER_JE + DORDER_EJ;

    // ST is the behavior of the search
    private final static String PARAM_ST = "st";
    private final static String ST_FORWARD = "0";
    private final static String ST_EXACT = "1";
    private final static String ST_BACKWARDS = "2";
    private final static String ST_FULL_TEXT = "3";
    private final static String ST_PARTIAL = "5";

    // Enabling and disabling of languages
    // Display will go by DORDER
    private final static String PARAM_DAILYJJ = "DailyJJ";
    private final static String PARAM_DAILYJE = "DailyJE";
    private final static String PARAM_DAILYEJ = "DailyEJ";
    private final static String SET_LANG = "checkbox";

    private final static String SANSEIDO_WORD_ID = "word";
    private final static String SANSEIDO_WORD_DEFINITION_ID = "wordBody";

    //TODO: Maybe something more graceful or renaming
    private final static String MULTIPLE_DEFINITION_REGEX = "▼";
    private final static String MULTIPLE_DEFINITION_SEPARATOR = "\n▼";

    private final static int RELATED_WORDS_TYPE_CLASS_INDEX = 0;
    private final static int RELATED_WORDS_VOCAB_INDEX = 1;
    private final static int RELATED_WORDS_TABLE_INDEX = 0;
    private final static String EXACT_WORD_REGEX = "(?<=［).*(?=］)";
    private final static String KANJI_REGEX = "[一-龯][ぁ-んァ-ン]*";
    private final static String KANA_REGEX = "[ぁ-んァ-ン]+";

    /**
     * Builds the URL for the desired word(s) to search for on Sanseido.
     *
     * @param word the Japanese word to search for
     * @param JJDic whether to return the definition in Japanese or not (else, English)
     * @return the Sanseido url created
     * @throws MalformedURLException
     */
    public static URL buildQueryURL(String word, boolean JJDic) throws MalformedURLException{

        Uri.Builder uriBuilder = Uri.parse(SANSEIDOU_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_ST, ST_EXACT)
                        .appendQueryParameter(PARAM_DORDER, DORDER_DEFAULT)
                        .appendQueryParameter(PARAM_WORD_QUERY, word);
        if(JJDic){
            uriBuilder.appendQueryParameter(PARAM_DAILYJJ, SET_LANG);
        }
        else{
            uriBuilder.appendQueryParameter(PARAM_DAILYJE, SET_LANG);
        }

        return new URL(uriBuilder.build().toString());
    }

    public static Document getSanseidoSource(URL searchURL) throws IOException{
        Document htmlTree = Jsoup.connect(searchURL.toString()).get();

        return htmlTree;
    }

    // Maybe unnecessary and just catch no words before this point
    public static String getWord(Document htmlTree){
        Element word = htmlTree.getElementById(SANSEIDO_WORD_ID);

        return word.text().toString();
    }

    public static String getDefinition(Document htmlTree){
        Element definitionParentElement = htmlTree.getElementById(SANSEIDO_WORD_DEFINITION_ID);
        // The definition is in a further div, single child
        String definition = "";

        if(definitionParentElement.children().size() > 0){
            definition = definitionParentElement.child(0).text();
        }

        //TODO: FIX REGEX
        definition = definition.replaceAll(MULTIPLE_DEFINITION_REGEX, MULTIPLE_DEFINITION_SEPARATOR);
        definition = definition.replaceFirst(MULTIPLE_DEFINITION_REGEX, MULTIPLE_DEFINITION_SEPARATOR);

        return definition;
    }

    //TODO: Create links to words in the related words table
    public static Map<String, Set<String>> getRelatedWords(Document htmlTree){
        Map<String, Set<String>> relatedWords = new HashMap<>();
        // The related words table is the first table in the HTML
        Element table = htmlTree.select("table").get(RELATED_WORDS_TABLE_INDEX);
        Elements rows = table.select("tr");

        // Preferred selecting by exact word first, but attempt others if it is a message input
        // like many that exist in the Sanseido searches that are not exact or if kana is
        // inputted
        Pattern exactWordPattern = Pattern.compile(EXACT_WORD_REGEX);
        Pattern kanjiWordPattern = Pattern.compile(KANJI_REGEX);
        Pattern kanaWordPattern = Pattern.compile(KANA_REGEX);

        // TODO: HANDLE ALL THE AWFUL INPUTS THAT THE FORWARD SEARCHING CAN HAVE
        Log.d("TEST", "" + rows.size());
        for (Element row : rows) {
            Elements columns = row.select("td");

            String dictionary = columns.get(RELATED_WORDS_TYPE_CLASS_INDEX).text().toString();
            if (!relatedWords.containsKey(dictionary)){
                relatedWords.put(dictionary, new HashSet<String>());
            }

            String tableEntry = columns.get(RELATED_WORDS_VOCAB_INDEX).text().toString();

            Matcher exactMatcher = exactWordPattern.matcher(tableEntry);
            Matcher kanjiMatcher = kanjiWordPattern.matcher(tableEntry);
            Matcher kanaMatcher = kanaWordPattern.matcher(tableEntry);

            if(exactMatcher.find()){
                relatedWords.get(dictionary).add(exactMatcher.group(0).toString());
            }
            else if (kanjiMatcher.find()){
                relatedWords.get(dictionary).add(kanjiMatcher.group(0).toString());
            }
            else if (kanaMatcher.find()){
                relatedWords.get(dictionary).add(kanaMatcher.group(0).toString());
            }
            else {
                relatedWords.get(dictionary).add(tableEntry);
            }
        }
        return relatedWords;
    }

    //TODO: Isolate the Kanji without kana
    //Use as part to getFurigana, likely
    public static String getKanji(String word){
        return "";
    }
    //TODO: Method to return Furigana in Anki format as string
    // Could use J-E dic for consistency instead of scraping.
    public static String getFurigana(String word){
        return "";
    }

    //TODO: Isolate readings
    public static String getReading(String word){
        return "";
    }

    //TODO: Separate and format each definition
    public static String[] getDefinitions(String definitions){
        //Many are separated by ▼, so can use that to split the string or as a regex.
        return null;
    }


}
