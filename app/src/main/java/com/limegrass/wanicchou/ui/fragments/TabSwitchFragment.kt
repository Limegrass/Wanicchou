package com.limegrass.wanicchou.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.limegrass.wanicchou.R
import com.limegrass.wanicchou.ui.adapter.ListPagerAdapter
import com.limegrass.wanicchou.util.replaceListPagerAdapter

class TabSwitchFragment : Fragment() {
    companion object {
        private val TAG : String = TabSwitchFragment::class.java.simpleName
        const val WORD_PAGER_ID : Int = 322
        const val NAVIGATION_PAGER_ID : Int = 420
    }
    private lateinit var wordButton : TextView
    private lateinit var relatedButton : TextView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.v(TAG, "Fragment: TabSwitch")
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_tab_switch,
                                    container,
                                    attachToRoot)
        wordButton = view.findViewById(R.id.btn_tab_word)
        relatedButton = view.findViewById(R.id.btn_tab_related)
        setWordButtonOnClick(view)
        setRelatedButtonOnClick(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pager = view.rootView.findViewById<ViewPager>(R.id.pager)
        val tabDots = view.rootView.findViewById<TabLayout>(R.id.tab_dots)
        tabDots.setupWithViewPager(pager, true)
        val fragments = listOf(DefinitionFragment(),
                VocabularyNoteFragment(),
                DefinitionNoteFragment())
        pager.adapter = ListPagerAdapter(fragmentManager!!,
                fragments,
                TabSwitchFragment.WORD_PAGER_ID)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setWordButtonOnClick(view : View){
        val wordButton = view.findViewById<TextView>(R.id.btn_tab_word)
        wordButton.setOnClickListener {
            val pager = view.rootView.findViewById<ViewPager>(R.id.pager)
            val adapter = pager.adapter as ListPagerAdapter
            if (adapter.id != WORD_PAGER_ID) {
                val fragments = listOf(DefinitionFragment(),
                        VocabularyNoteFragment(),
                        DefinitionNoteFragment())
                val replacementAdapter = ListPagerAdapter(fragmentManager!!,
                        fragments,
                        WORD_PAGER_ID)
                pager.replaceListPagerAdapter(replacementAdapter)
                relatedButton.setBackgroundColor(
                        ContextCompat.getColor(context!!,
                                R.color.color_grey_inactive))
                wordButton.setBackgroundColor(
                        ContextCompat.getColor(context!!,
                                R.color.color_aqua))
            }
        }
    }

    private fun setRelatedButtonOnClick(view: View){
        val relatedButton = view.findViewById<TextView>(R.id.btn_tab_related)
        relatedButton.setOnClickListener {
            val pager = view.rootView.findViewById<ViewPager>(R.id.pager)
            val adapter = pager.adapter as ListPagerAdapter
            if (adapter.id != NAVIGATION_PAGER_ID) {
                val fragments = listOf(RelatedFragment(),
                                       TagFragment())
                val replacementAdapter = ListPagerAdapter(fragmentManager!!,
                                                          fragments,
                                                          NAVIGATION_PAGER_ID)
                pager.replaceListPagerAdapter(replacementAdapter)
                wordButton.setBackgroundColor(
                        ContextCompat.getColor(context!!,
                                               R.color.color_grey_inactive))
                relatedButton.setBackgroundColor(
                        ContextCompat.getColor(context!!,
                                               R.color.color_aqua))
            }
        }
    }
}

