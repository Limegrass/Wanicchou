package data.vocab.models;

public interface Vocabulary {
    String getWord();
    String getReading();
    String getDefinition();
    String getPitch();
    DictionaryType getDictionaryType();

    void setWord(String word);
    void setReading(String reading);
    void setDefinition(String definition);
    void setPitch(String pitch);
    void setDictionaryType(DictionaryType dictionaryType);
}
