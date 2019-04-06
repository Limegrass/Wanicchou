package com.limegrass.wanicchou.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ListPagerAdapter(private val fragmentManager: FragmentManager,
                       private var fragments : List<Fragment>,
                       val id : Int)
    : FragmentPagerAdapter(fragmentManager) {

    @Throws(IndexOutOfBoundsException::class)
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun clearFragments() {
        val transaction = fragmentManager.beginTransaction()
        for(fragment in fragments){
            transaction.remove(fragment)
        }
        transaction.commitNow()
    }
}
