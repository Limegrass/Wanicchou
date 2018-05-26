package data.vocab.search;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
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

import data.vocab.DictionaryType;
import data.vocab.JapaneseVocabulary;

/**
 * Created by Limegrass on 3/19/2018.
 */

public class SanseidoSearch implements Parcelable {
    private final static String TAG = SanseidoSearch.class.getSimpleName();

    private final String SANSEIDO_WORD_ID = "word";
    private final String SANSEIDO_WORD_DEFINITION_ID = "wordBody";
    //TODO: Maybe something more graceful or renaming
    private final String MULTIPLE_DEFINITION_REGEX = "▼";
    private final String MULTIPLE_DEFINITION_SEPARATOR = "\n▼";
    
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

    private final static int RELATED_WORDS_TYPE_CLASS_INDEX = 0;
    private final static int RELATED_WORDS_VOCAB_INDEX = 1;
    private final static int RELATED_WORDS_TABLE_INDEX = 0;

    //TODO: Refactor to it uses the enum type
    private Map<String, Set<String>> relatedWords;
    private JapaneseVocabulary vocabulary;


    // TODO: Fix 宝物 not being able to redirect to itself properly. Goes to 宝
    // I can do this by stripping all non kana/kanji chars from the word source and putting it in the search.

    /**
     * Constructor to create an object containing the information retrieved from Sanseido from
     * a search given a word to search.
     * @param wordToSearch the desired word to search.
     * @throws IOException
     */
    public SanseidoSearch(String wordToSearch, DictionaryType dictionaryType) throws IOException {
        //TODO: Refactor the URL and vocab to reference a saved pref var for if it's JJ, JE, or EJ
        URL url = buildQueryURL(wordToSearch, dictionaryType);
        Document html = fetchSanseidoSource(url);
        relatedWords = findRelatedWords(html);
        vocabulary = new JapaneseVocabulary(
                findWordSource(html),
                findDefinitionSource(html),
                dictionaryType);
    }


    public SanseidoSearch(JapaneseVocabulary japaneseVocabulary, Map<String, Set<String> > relatedWords){
        this.vocabulary = japaneseVocabulary;
        this.relatedWords = relatedWords;
    }

    /**
     * Getter for the vocabulary word searched.
     * @return the string representing the vocabulary word found.
     */
    public JapaneseVocabulary getVocabulary(){
        return vocabulary;
    }

    /**
     * Getter for the list of related words from the search.
     * @return A map mapping the Sanseido dictionary the word exists in and their related words.
     */
    public Map<String, Set<String>> getRelatedWords(){
        return relatedWords;
    }

    /**
     * Builds the URL for the desired word(s) to search for on Sanseido.
     *
     * @param word the Japanese word to search for
     * @param dictionaryType which dictionary to search from Sanseido
     * @return the Sanseido url created
     * @throws MalformedURLException
     */
    private URL buildQueryURL(String word, DictionaryType dictionaryType) throws MalformedURLException{

        Uri.Builder uriBuilder = Uri.parse(SANSEIDOU_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_ST, ST_EXACT)
                        .appendQueryParameter(PARAM_DORDER, DORDER_DEFAULT)
                        .appendQueryParameter(PARAM_WORD_QUERY, word);
        switch (dictionaryType) {

            case JJ:
                uriBuilder.appendQueryParameter(PARAM_DAILYJJ, SET_LANG);
                break;
            case JE:
                uriBuilder.appendQueryParameter(PARAM_DAILYJE, SET_LANG);
                break;
            case EJ:
                uriBuilder.appendQueryParameter(PARAM_DAILYEJ, SET_LANG);
                break;
            default:
                uriBuilder.appendQueryParameter(PARAM_DAILYJJ, SET_LANG);
        }

        return new URL(uriBuilder.build().toString());
    }


    /**
     * Helper method to create an HTTP request to Sanseido for a given URL.
     * @param searchURL the URL of the word search to be performed
     * @return A Jsoup html document tree of the html source from the search.
     * @throws IOException
     */
    private Document fetchSanseidoSource(URL searchURL) throws IOException{
        Document htmlTree = Jsoup.connect(searchURL.toString()).get();

        return htmlTree;
    }

    /**
     * Helper method to isolate the related words of a search from the raw html source.
     * @param html the raw html jsoup document tree.
     * @return a map of related words in a set with the key being the dictionary they exist in.
     */
    private Map<String, Set<String>> findRelatedWords(Document html){
        Map<String, Set<String>> relatedWords = new HashMap<>();
        // The related words table is the first table in the HTML
        Element table = html.select("table").get(RELATED_WORDS_TABLE_INDEX);
        Elements rows = table.select("tr");

        // Preferred selecting by exact word first, but attempt others if it is a message input
        // like many that exist in the Sanseido searches that are not exact or if kana is
        // inputted

        // TODO: HANDLE ALL THE AWFUL INPUTS THAT THE FORWARD SEARCHING CAN HAVE
        // TODO: Refactor this to use DictionaryType enum
        Log.d(TAG, "" + rows.size());
        for (Element row : rows) {
            Elements columns = row.select("td");

            String dictionary = columns.get(RELATED_WORDS_TYPE_CLASS_INDEX).text().toString();
            if (!relatedWords.containsKey(dictionary)){
                relatedWords.put(dictionary, new HashSet<String>());
            }

            String tableEntry = columns.get(RELATED_WORDS_VOCAB_INDEX).text().toString();
            String isolatedWord = JapaneseVocabulary.isolateWord(tableEntry);

            relatedWords.get(dictionary).add(isolatedWord);
        }
        return relatedWords;
    }

    /**
     * Retrieve the searched word from the html source
     * @param html the html source
     * @return the word searched for
     */
    private String findWordSource(Document html){
        Element word = html.getElementById(SANSEIDO_WORD_ID);

        return word.text().toString();
    }

    /**
     * A helper method to isolate the source text of the definition of the word searched.
     * @param html the jsoup html document tree.
     * @return
     */
    private String findDefinitionSource(Document html){
        Element definitionParentElement = html.getElementById(SANSEIDO_WORD_DEFINITION_ID);
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


    /*
     Parcelable methods, needed to pass information between activities.
     */
    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(vocabulary);
        parcel.writeValue(relatedWords);
    }

    public static final Parcelable.Creator<SanseidoSearch> CREATOR
            = new Parcelable.Creator<SanseidoSearch>(){
        @Override
        public SanseidoSearch createFromParcel(Parcel parcel) {
            return new SanseidoSearch(parcel);
        }

        @Override
        public SanseidoSearch[] newArray(int size) {
            return new SanseidoSearch[size];
        }
    };

    private SanseidoSearch(Parcel parcel){
        final ClassLoader classLoader = getClass().getClassLoader();
        vocabulary = (JapaneseVocabulary) parcel.readValue(classLoader);
        relatedWords = (Map<String, Set<String>>) parcel.readValue(classLoader);
    }

}
