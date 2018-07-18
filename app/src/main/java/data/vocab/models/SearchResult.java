package data.vocab.models;

import android.os.Parcelable;

import java.util.List;

import data.vocab.WordListEntry;

/**
 * Interface for the necessary components to return from a search.
 */
public interface SearchResult extends Parcelable {
    Vocabulary getVocabulary();
    List<WordListEntry> getRelatedWords();
}
