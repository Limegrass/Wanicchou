package data.vocab.models;

import java.util.List;

public interface DictionaryType {
    String toDisplayText();
    String toKey();
    DictionaryType[] getAllDictionaryTypes();
}
