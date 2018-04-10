package data;

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

    private String wordSource;
    private String definitionSource;
    private Map<String, Set<String>> relatedWords;
    private JapaneseVocabulary vocabulary;


    public SanseidoSearch(String wordToSearch) throws IOException {
        URL url = buildQueryURL(wordToSearch, true);
        Document html = fetchSanseidoSource(url);
        wordSource = findWordSource(html);
        definitionSource = findDefinitionSource(html);
        relatedWords = findRelatedWords(html);
        vocabulary = new JapaneseVocabulary(wordSource, definitionSource);
    }

    public String getWordSource(){
        return wordSource;
    }

    public JapaneseVocabulary getVocabulary(){
        return vocabulary;
    }

    public Map<String, Set<String>> getRelatedWords(){
        return relatedWords;
    }

    /**
     * Builds the URL for the desired word(s) to search for on Sanseido.
     *
     * @param word the Japanese word to search for
     * @param JJDic whether to return the definition in Japanese or not (else, English)
     * @return the Sanseido url created
     * @throws MalformedURLException
     */
    private URL buildQueryURL(String word, boolean JJDic) throws MalformedURLException{

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



    private Document fetchSanseidoSource(URL searchURL) throws IOException{
        Document htmlTree = Jsoup.connect(searchURL.toString()).get();

        return htmlTree;
    }

    //TODO: Create links to words in the related words table
    private Map<String, Set<String>> findRelatedWords(Document htmlTree){
        Map<String, Set<String>> relatedWords = new HashMap<>();
        // The related words table is the first table in the HTML
        Element table = htmlTree.select("table").get(RELATED_WORDS_TABLE_INDEX);
        Elements rows = table.select("tr");

        // Preferred selecting by exact word first, but attempt others if it is a message input
        // like many that exist in the Sanseido searches that are not exact or if kana is
        // inputted

        // TODO: HANDLE ALL THE AWFUL INPUTS THAT THE FORWARD SEARCHING CAN HAVE
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


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(wordSource);
        parcel.writeString(definitionSource);
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
        wordSource = parcel.readString();
        definitionSource = parcel.readString();
        vocabulary = (JapaneseVocabulary) parcel.readValue(classLoader);
        relatedWords = (Map<String, Set<String>>) parcel.readValue(classLoader);
    }

}
