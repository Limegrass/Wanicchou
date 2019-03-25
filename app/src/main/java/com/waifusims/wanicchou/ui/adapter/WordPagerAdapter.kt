package com.waifusims.wanicchou.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.waifusims.wanicchou.ui.fragments.*

class WordPagerAdapter(fragmentManager: FragmentManager)
    : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DefinitionFragment()
            1 -> RelatedFragment()
            2 -> TagFragment()
            3 -> VocabularyNoteFragment()
            4 -> DefinitionNoteFragment()
            else -> throw IndexOutOfBoundsException("Invalid position for Word Pager.")
        }
    }

    override fun getCount(): Int {
        return 5
    }

}