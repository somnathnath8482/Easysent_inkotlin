package com.easy.kotlintest.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentsList = ArrayList<Fragment>()
    private val fragmentsTitle = ArrayList<String>()

    override fun getItemCount(): Int {
        return fragmentsList.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position]
    }
    fun addFragment(chatsFragment: Fragment?, chats: String?) {
        fragmentsList.add(chatsFragment!!)
        fragmentsTitle.add(chats!!)
    }
}