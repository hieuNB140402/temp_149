package com.love.compatibility.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.love.compatibility.ui.home.fragment.HomeFragment
import com.love.compatibility.ui.home.fragment.StatusFragment

class HomeAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            else -> StatusFragment()
        }
    }

    override fun getItemCount(): Int = 2
}