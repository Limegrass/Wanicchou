package data.vocab.search;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import data.vocab.DictionaryType;
import data.vocab.JapaneseVocabulary;
import data.vocab.MatchType;

//TODO: Equals, Hashcode methods

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

    // Enabling and disabling of languages
    // Display will go by DORDER
    private final static String PARAM_DIC_PREFIX = "Daily";
    private final static String SET_LANG = "checkbox";

    protected final static int RELATED_WORDS_TYPE_CLASS_INDEX = 0;
    protected final static int RELATED_WORDS_VOCAB_INDEX = 1;
    protected final static int RELATED_WORDS_TABLE_INDEX = 0;

    //TODO: Refactor to it uses the enum type
    private List<RelatedWordEntry> relatedWords;
    private JapaneseVocabulary vocabulary;


    // TODO: Fix 宝物 not being able to redirect to itself properly. Goes to 宝
    // I can do this by stripping all non kana/kanji chars from the word source and putting it in the search.

    /**
     * Constructor to create an object containing the information retrieved from Sanseido from
     * a search given a word to search.
     * @param wordToSearch the desired word to search.
     * @throws IOException if the search cannot be completed
     */
    public SanseidoSearch(String wordToSearch,
                          DictionaryType dictionaryType,
                          MatchType matchType)
            throws IOException {
        //TODO: Fix relatedWords searching, as it is not working properly for forwards search
        if(TextUtils.isEmpty(wordToSearch)) {
            throw new IllegalArgumentException("Search term cannot be empty!");
        }
        if(dictionaryType == null){
            throw new IllegalArgumentException("Dictionary Type cannot be null!");
        }
        if(matchType == null){
            throw new IllegalArgumentException("Match Type cannot be null!");
        }

        URL url = buildQueryURL(wordToSearch, dictionaryType, matchType);
        Document html = fetchSanseidoSource(url);
        relatedWords = findRelatedWords(html);
        vocabulary = new JapaneseVocabulary(
                findWordSource(html),
                findDefinitionSource(html),
                dictionaryType);
    }

    public SanseidoSearch(String html, DictionaryType dictionaryType){
        this(Jsoup.parse(html), dictionaryType);
    }

    public SanseidoSearch(Document html, DictionaryType dictionaryType){
        relatedWords = findRelatedWords(html);
        vocabulary = new JapaneseVocabulary(findWordSource(html),
                findDefinitionSource(html),
                dictionaryType);
    }

    /**
     * Constructs a search object from a given vocab and it's related words
     * @param japaneseVocabulary The vocabulary with it's word-definition pair
     * @param relatedWords Words related to the vocabulary specific to it's search type.
     */
    public SanseidoSearch(JapaneseVocabulary japaneseVocabulary, List<RelatedWordEntry> relatedWords){
        this.vocabulary = japaneseVocabulary;
        this.relatedWords = relatedWords;
    }

    // ======================== GETTERS AND SETTERS ==================================

    /**
     * Getter for the vocabulary word searched.
     * @return the string representing the vocabulary word found.
     */
    public JapaneseVocabulary getVocabulary(){
        return vocabulary;
    }

    protected void setVocabulary(JapaneseVocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public List<RelatedWordEntry> getRelatedWords() {
        return relatedWords;
    }

    protected void setRelatedWords(List<RelatedWordEntry> relatedWords) {
        this.relatedWords = relatedWords;
    }


    // ================================ HELPERS ===================================

    /**
     * Builds the URL for the desired word(s) to search for on Sanseido.
     *
     * @param word the Japanese word to search for
     * @param dictionaryType which dictionary to search from Sanseido
     * @return the Sanseido url created
     * @throws MalformedURLException if a search url cannot be built
     */
    protected URL buildQueryURL(String word,
                              DictionaryType dictionaryType,
                              MatchType matchType)
            throws MalformedURLException{

        Uri.Builder uriBuilder = Uri.parse(SANSEIDOU_BASE_URL).buildUpon();

        uriBuilder.appendQueryParameter(PARAM_ST, matchType.sanseidoKey());
        uriBuilder.appendQueryParameter(PARAM_DORDER, DORDER_DEFAULT);
        uriBuilder.appendQueryParameter(PARAM_WORD_QUERY, word);
        uriBuilder.appendQueryParameter(PARAM_DIC_PREFIX + dictionaryType.toString(), SET_LANG);

        return new URL(uriBuilder.build().toString());
    }


    /**
     * Helper method to create an HTTP request to Sanseido for a given URL.
     * @param searchURL the URL of the word search to be performed
     * @return A Jsoup html document tree of the html source from the search.
     * @throws IOException if the search cannot be completed.
     */
    private Document fetchSanseidoSource(URL searchURL) throws IOException{
        return Jsoup.connect(searchURL.toString()).get();
    }

    /**
     * Helper method to isolate the related words of a search from the raw html source.
     * @param html the raw html jsoup document tree.
     * @return a map of related words in a set with the key being the dictionary they exist in.
     */
    private List<RelatedWordEntry> findRelatedWords(Document html){
        List<RelatedWordEntry> relatedWordEntries = new ArrayList<>();

        // The related words table is the first table in the HTML
        Element table = html.select("table").get(RELATED_WORDS_TABLE_INDEX);
        Elements rows = table.select("tr");

        // Preferred selecting by exact word first, but attempt others if it is a message input
        // like many that exist in the Sanseido searches that are not exact or if kana is
        // inputted

        // TODO: HANDLE ALL THE AWFUL INPUTS THAT THE FORWARD SEARCHING CAN HAVE
        Log.d(TAG, String.valueOf(rows.size()));
        for (Element row : rows) {

            Elements columns = row.select("td");

            String dictionaryTypeString = columns.get(RELATED_WORDS_TYPE_CLASS_INDEX).text();

            //Essentially using DictionaryType enum by substringing out the brackets 【 】
            if (dictionaryTypeString != null){
                dictionaryTypeString = dictionaryTypeString.substring(1, dictionaryTypeString.length()-1);

                DictionaryType dictionaryType =
                        DictionaryType.fromJapaneseDictionaryKanji(dictionaryTypeString);

                String tableEntry = columns.get(RELATED_WORDS_VOCAB_INDEX).text();
                String isolatedWord = JapaneseVocabulary.isolateWord(tableEntry);

                relatedWordEntries.add(new RelatedWordEntry(isolatedWord, dictionaryType));
            }
        }
        return relatedWordEntries;
    }

    /**
     * Retrieve the searched word from the html source
     * @param html the html source
     * @return the word searched for
     */
    private String findWordSource(Document html){
        Element word = html.getElementById(SANSEIDO_WORD_ID);

        return word.text();
    }

    /**
     * A helper method to isolate the source text of the definition of the word searched.
     * @param html the jsoup html document tree.
     * @return the raw definition source
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


    // ==================================== PARCELABLE ========================================
    /**
     * Describes contents for Parcelable.
     * @return The hashcode of the object.
     */
    @Override
    public int describeContents() {
        return hashCode();
    }


    /**
     * Parcelization of the search object
     * @param parcel The parcel to write to.
     * @param i Flags for parcelization.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(vocabulary);
        parcel.writeValue(relatedWords);
    }

    /**
     * Creator for parcelization.
     */
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

    /**
     * Constructor from a parcel.
     * @param parcel The parcel to read from.
     */
    private SanseidoSearch(Parcel parcel) {
        final ClassLoader classLoader = getClass().getClassLoader();
        vocabulary = (JapaneseVocabulary) parcel.readValue(classLoader);
        relatedWords = (List<RelatedWordEntry>) parcel.readValue(classLoader);
    }



}
