package data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Limegrass on 4/4/2018.
 */

public class JapaneseVocabulary implements Parcelable {

    // Regexes, not sure if they should be const static.
    // Most vocab are enclosed in the braces.
    public static final String EXACT_WORD_REGEX = "(?<=［).*(?=］)";

    // Try to find a word beginning with or enclosed with Kanji
    public static final String WORD_WITH_KANJI_REGEX =
            "\\p{Han}+[\\p{Hiragana}|\\p{Katakana}]*\\p{Han}*";

    // For finding only the kana of a word.
    public static final String KANA_REGEX = "[\\p{Hiragana}|\\p{Katakana}]+";

    public static final String READING_REGEX =
            "[\\p{Hiragana}|\\p{Katakana}]+(?=($|[\\p{Han}０-９]|\\d|\\s))";
    public static final String TONE_REGEX = "[\\d０-９]+";

    // Some messy dictionary entries have triangles in 
    public static final String TRIANGLES_REGEX = "[△▲]";

    private String word;
    private String furigana;
    private String reading;
    private String defintion;
    private String pitch;

    public JapaneseVocabulary(String wordSource, String definitionSource){
        defintion = definitionSource;
        word = isolateWord(wordSource);
        reading = isolateReading(wordSource);
        pitch = isolatePitch(wordSource);
        furigana = isolateFurigana(word, reading);
    }

    // TODO: Maybe do something in Sanseido search for related words so this can be private

    /**
     * Isolates the full word from the possibly messy Sanseido html source
     * @param wordSource the raw string from the html source
     * @return The full word isolated from any furigana readings or tones
     */
    public static String isolateWord(String wordSource){
        wordSource = wordSource.replaceAll(TRIANGLES_REGEX, "");
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

    public String getWord() {
        return word;
    }

    public String getFurigana() {
        return furigana;
    }

    public String getReading() {
        return reading;
    }

    public String getDefintion() {
        return defintion;
    }
    public String getTone(){
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



    //TODO: Method to return Furigana in Anki format as string
    // Could use J-E dic for consistency instead of scraping.
    private String isolateFurigana(String isolatedWord, String isolatedReading){
        if (isolatedWord.equals(isolatedReading)){
            return isolatedReading;
        }

        return isolatedWord + "[" + isolatedReading + "]";
    }

    private String isolateReading(String wordSource){
        if(wordSource == null || wordSource.equals("")){
            return "";
        }

        Matcher readingMatcher = Pattern.compile(READING_REGEX).matcher(wordSource);
        if(readingMatcher.find()){
            return readingMatcher.group(0).toString();
        }
        return wordSource;
    }

    //TODO: Separate and format each definition
    public static String[] getDefinitions(String definitions){
        //Many are separated by ▼, so can use that to split the string or as a regex.
        return null;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(word);
        parcel.writeString(furigana);
        parcel.writeString(reading);
        parcel.writeString(defintion);
        parcel.writeString(pitch);
    }

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

    private JapaneseVocabulary(Parcel parcel){
        word = parcel.readString();
        furigana = parcel.readString();
        reading = parcel.readString();
        defintion = parcel.readString();
        pitch = parcel.readString();
    }
}
