package data.vocab.jp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.room.voc.VocabularyEntity;
import data.vocab.models.DictionaryType;
import data.vocab.models.Vocabulary;

/**
 * Created by Limegrass on 4/4/2018.
 */

public class JapaneseVocabulary implements Parcelable, Vocabulary {
    // Words to test: 計る, アニメ、animation, 雪害

    // Should these be in the Android Strings file?
    // Regexes, not sure if they should be const static.
    // Most vocab are enclosed in the braces.
    private static final String EXACT_WORD_REGEX = "(?<=［).*(?=］)";
    private static final String EXACT_EJ_REGEX = ".*(?=［.*］)";

    // Try to find a word beginning with or enclosed with Kanji
    private static final String WORD_WITH_KANJI_REGEX =
            "\\p{script=Han}+[\\p{script=Hiragana}|\\p{script=Katakana}]*\\p{script=Han}*";

    // For finding only the kana of a word.
    private static final String KANA_REGEX = "[\\p{script=Hiragana}|\\p{script=Katakana}]+";

    private static final String READING_REGEX =
            "[\\p{script=Hiragana}|\\p{script=Katakana}]+($|[\\p{script=Han}０-９]|\\d|\\s)*?";
    private static final String TONE_REGEX = "[\\d０-９]+";

    // Some messy dictionary entries have triangles in 
    private static final String SEPARATOR_FRAGMENTS_REGEX = "[△▲･・]";

    private String word;
    private String reading;
    private String definition;
    private String pitch;
    private DictionaryType dictionaryType;

    /**
     * Constructor given a string containing the word and a string containing the definition.
     * @param wordSource a string that contains the source of the word.
     * @param definitionSource a string containing the definition of the word.
     */
    public JapaneseVocabulary(String wordSource, String definitionSource, DictionaryType dictionaryType){
        wordSource = wordSource.trim();
        definitionSource = definitionSource.trim();

        definition = definitionSource;
        word = isolateWord(wordSource, dictionaryType);
        reading = isolateReading(wordSource, dictionaryType);
        pitch = isolatePitch(wordSource);
        this.dictionaryType = dictionaryType;
    }

    /**
     * Constructs a Japanese Vocab object from an element from the Vocabulary database.
     * @param entity An entry from the Vocabulary Database
     */
    public JapaneseVocabulary(VocabularyEntity entity){
        word = entity.getWord();
        reading = entity.getReading();
        definition = entity.getDefinition();
        pitch = entity.getPitch();
        dictionaryType = JapaneseDictionaryType.fromKey(entity.getDictionaryType());
    }

    /**
     * Constructor for invalid word searches, to avoid repeatedly requesting invalid searches.
     * @param invalidWord The word whose search completed but failed.
     * @param dictionaryType The dictionary type the search completed but failed under.
     */
    public JapaneseVocabulary(String invalidWord, DictionaryType dictionaryType){
        definition = "N/A";
        word = invalidWord;
        reading = "N/A";
        pitch = "N/A";
        this.dictionaryType = dictionaryType;
    }

    /**
     * Checks if the two JapaneseVocabulary objects have the same word, reading, and definition.
     * @param obj another JapaneseVocabulary instance
     * @return whether the two instances has the same word, reading, and definition
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }

        if(!(obj instanceof JapaneseVocabulary)){
            return false;
        }
        JapaneseVocabulary other = (JapaneseVocabulary) obj;

        //furigana is generated, pitch should not change
        //Maybe not include definition in case of different site definitions or formatting.
        return word.equals(other.word)
                && reading.equals(other.reading)
                && (dictionaryType == other.dictionaryType)
                && definition.equals(other.definition);
    }

    /**
     * Hash code function
     * @return a hashcode for the vocabulary word
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + word.hashCode();
        hash = 31 * hash + reading.hashCode();
        hash = 31 * hash + definition.hashCode();
        hash = 31 * hash + pitch.hashCode();
        return hash;
    }

    // TODO: Maybe do something in Sanseido search for related words so this can be private

    /**
     * Isolates the full word from the possibly messy Sanseido html source
     * @param wordSource the raw string from the html source
     * @return The full word isolated from any furigana readings or tones
     */
    public static String isolateWord(String wordSource, DictionaryType dictionaryType){
        wordSource = wordSource.replaceAll(SEPARATOR_FRAGMENTS_REGEX, "");
        if(dictionaryType == JapaneseDictionaryType.EJ){
            Matcher ejMatcher = Pattern.compile(EXACT_EJ_REGEX).matcher(wordSource);
            if(ejMatcher.find()){
                return ejMatcher.group(0).toString();
            }
        }
        Matcher exactMatcher = Pattern.compile(EXACT_WORD_REGEX).matcher(wordSource);
        Matcher kanjiMatcher = Pattern.compile(WORD_WITH_KANJI_REGEX).matcher(wordSource);
        Matcher kanaMatcher = Pattern.compile(KANA_REGEX).matcher(wordSource);

        if(exactMatcher.find()){
            return exactMatcher.group(0).toString();
        }
        else if (kanjiMatcher.find()){
            return kanjiMatcher.group(0).toString();
        }
        else if (kanaMatcher.find()){
            return kanaMatcher.group(0).toString();
        }
        else {
            return wordSource;
        }
    }

    /**
     * Getter for the Japanese word (in Kanji or it's most common dictionary script).
     * @return the Japanese word in the form it appeared in the dictionary.
     */
    public String getWord() {
        return word;
    }

    /**
     * Gets the dictionary type of the current vocabulary's dictionary entry.
     * @return The dictionary type of the word-definition pair.
     */
    public DictionaryType getDictionaryType(){
        return dictionaryType;
    }

    /**
     * Generates an Anki format furigana string from the word and reading saved.
     * @return a string for Anki's furigana display.
     */
    public String getFurigana() {
        if (word.equals(reading)){
            return reading;
        }

        return word + "[" + reading + "]";
    }

    /**
     * Getter for the reading in kana of the word.
     * @return a string of the kana reading of the vocabulary word.
     */
    public String getReading() {
        return reading;
    }

    /**
     * Getter for the definition of the word.
     * @return a string of the definition of the word.
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Getter for the pitch of the word.
     * @return a string representing the pitch of the word.
     */
    public String getPitch(){
        return pitch;
    }

    /**
     * Finds the tone from the given word source information
     * @param wordSource the raw information about the word
     * @return a string of the pitch of the word
     */
    private String isolatePitch(String wordSource){
        if(wordSource == null || wordSource.equals("")){
            return "";
        }

        String tone = "";
        Matcher toneMatcher = Pattern.compile(TONE_REGEX).matcher(wordSource);
        if (toneMatcher.find()){
            tone = toneMatcher.group(0).toString();
        }
        return tone;
    }

    /**
     * Helper method to isolate the reading of a Japanese vocabulary word from its source string.
     * @param wordSource the raw string containing the vocabulary word.
     * @return a string with the isolated kana reading of the word.
     */
    private String isolateReading(String wordSource, DictionaryType dictionaryType){
        if(wordSource == null || wordSource.equals("")){
            return "";
        }
        // Dic uses images to show pronunciations in the International Phonetic Alphabet
        // Maybe work around some other time
        if(dictionaryType == JapaneseDictionaryType.EJ){
            int splitPos = wordSource.indexOf('[');
            if (splitPos < 0){
                splitPos = wordSource.indexOf('［');
            }
            if (splitPos > 0){
                return wordSource.substring(0, splitPos);
            }
        }


        wordSource = wordSource.replaceAll(SEPARATOR_FRAGMENTS_REGEX, "");
        Matcher readingMatcher = Pattern.compile(READING_REGEX).matcher(wordSource);
        if(readingMatcher.find()){
            return readingMatcher.group(0).toString();
        }
        return wordSource;
    }


    /**
     * Part of necessary methods to override to make the object parcelable.
     * @return the hash code of the JapaneseVocabulary.
     */
    @Override
    public int describeContents() {
        return hashCode();
    }

    /**
     * Parcelization of the JapaneseObject, called when the object is
     * passed between activities.
     * @param parcel Parcel to write to.
     * @param i Flags for the parcelization.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(word);
        parcel.writeString(reading);
        parcel.writeString(definition);
        parcel.writeString(pitch);
        parcel.writeValue(dictionaryType);
    }

    /**
     * Creator for the parcels.
     */
    public static final Parcelable.Creator<JapaneseVocabulary> CREATOR
            = new Parcelable.Creator<JapaneseVocabulary>(){
        @Override
        public JapaneseVocabulary createFromParcel(Parcel parcel) {
            return new JapaneseVocabulary(parcel);
        }

        @Override
        public JapaneseVocabulary[] newArray(int size) {
            return new JapaneseVocabulary[size];
        }
    };

    /**
     * constructor to unpack the parcel information for passing between activities.
     * @param parcel The parcel to read from an construct the JapaneseVocabulary object.
     */
    private JapaneseVocabulary(Parcel parcel){
        final ClassLoader classLoader = getClass().getClassLoader();
        word = parcel.readString();
        reading = parcel.readString();
        definition = parcel.readString();
        pitch = parcel.readString();
        dictionaryType = (JapaneseDictionaryType) parcel.readValue(classLoader);
    }
}
