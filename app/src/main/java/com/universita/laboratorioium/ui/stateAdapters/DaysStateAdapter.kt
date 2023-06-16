package com.universita.laboratorioium.ui.stateAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.universita.laboratorioium.ui.fragments.DayFragment

class DaysStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return NUM_ITEMS
    }

    override fun createFragment(position: Int): Fragment {
        return DayFragment.newInstance(position)
    }

    companion object {
        private const val NUM_ITEMS = 5
    }
}