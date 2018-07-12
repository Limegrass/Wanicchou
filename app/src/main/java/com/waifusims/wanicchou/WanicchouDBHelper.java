package com.waifusims.wanicchou;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

import data.room.context.ContextViewModel;
import data.room.notes.NoteViewModel;
import data.room.rel.RelatedWordViewModel;
import data.room.voc.VocabularyViewModel;
import data.vocab.models.SearchResult;

public class WanicchouDBHelper {
    private VocabularyViewModel mVocabViewModel;
    private RelatedWordViewModel mRelatedWordViewModel;
    private NoteViewModel mNoteViewModel;
    private ContextViewModel mContextViewModel;

    public WanicchouDBHelper(FragmentActivity activity){
        mVocabViewModel = ViewModelProviders.of(activity).get(VocabularyViewModel.class);
        mRelatedWordViewModel = ViewModelProviders.of(activity).get(RelatedWordViewModel.class);
        mNoteViewModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
        mContextViewModel = ViewModelProviders.of(activity).get(ContextViewModel.class);
    }

    public void addInvalidWord(SearchResult searchResult){


    }


}
