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
    public static final String EXACT_WORD_REGEX = "(?<=［).*(?=］)";
    public static final String KANJI_REGEX = "[一-龯][ぁ-んァ-ンー]*";
    public static final String KANJI_ONLY_REGEX = "[一-龯]*";
    public static final String KANA_REGEX = "[ぁ-んァ-ンー]+";
    public static final String READING_REGEX = "[ぁ-んァ-ンー]+(?=($|[一-龯０-９]|\\d|\\s))";
    //TODO: TONE REGEX AND SET TONE

    private String word;
    private String furigana;
    private String kanji;
    private String reading;
    private String defintion;
    private String tone;

    public JapaneseVocabulary(String wordSource, String definitionSource){
        defintion = definitionSource;
        word = isolateWord(wordSource);
        furigana = isolateFurigana(word);
        kanji = isolateKanji(word);
        reading = isolateReading(word);
    }

    // TODO: Maybe do something in Sanseido search for related words so this can be private

    /**
     * Isolates the full word from the possibly messy Sanseido html source
     * @param wordSource the raw string from the html source
     * @return The full word isolated from any furigana readings or tones
     */
    public static String isolateWord(String wordSource){
        Matcher exactMatcher = Pattern.compile(EXACT_WORD_REGEX).matcher(wordSource);
        Matcher kanjiMatcher = Pattern.compile(KANJI_REGEX).matcher(wordSource);
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

    public String getKanji() {
        return kanji;
    }

    public String getReading() {
        return reading;
    }

    public String getDefintion() {
        return defintion;
    }
    public String getTone(){
        return tone;
    }


    private String isolateKanji(String word){
        if(word == null){
            return "";
        }

        Matcher kanjiOnlyMatcher = Pattern.compile(KANJI_ONLY_REGEX).matcher(word);
        if (kanjiOnlyMatcher.find()){
            return kanjiOnlyMatcher.group(0).toString();
        }
        return word;
    }

    //TODO: Method to return Furigana in Anki format as string
    // Could use J-E dic for consistency instead of scraping.
    private String isolateFurigana(String word){
        if(word == null){
            return "";
        }

        String isolatedWord = isolateWord(word);
        String kanji = isolateKanji(word);
        return isolatedWord.substring(0, kanji.length()-1);
    }

    private String isolateReading(String word){
        if(word == null){
            return "";
        }

        Matcher readingMatcher = Pattern.compile(READING_REGEX).matcher(word);
        if(readingMatcher.find()){
            return readingMatcher.group(0).toString();
        }
        return word;
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
        parcel.writeString(kanji);
        parcel.writeString(reading);
        parcel.writeString(defintion);
        parcel.writeString(tone);
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
        kanji = parcel.readString();
        reading = parcel.readString();
        defintion = parcel.readString();
        tone = parcel.readString();
    }
}
