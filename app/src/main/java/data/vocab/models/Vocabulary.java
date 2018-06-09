package data.vocab.models;

public interface Vocabulary {
    String getWord();
    String getReading();
    String getDefinition();
    String getPitch();
    DictionaryType getDictionaryType();
}
