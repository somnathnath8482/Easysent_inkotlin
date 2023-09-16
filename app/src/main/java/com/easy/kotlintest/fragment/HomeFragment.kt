package com.easy.kotlintest.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.easy.kotlintest.R
import com.easy.kotlintest.adapter.ViewPagerAdapter
import com.easy.kotlintest.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    lateinit var adapter: ViewPagerAdapter
    lateinit var binding: FragmentHomeBinding
    lateinit var contexts: Context
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.contexts = requireContext()
        this.activity = requireActivity()
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        adapter.addFragment(ChatsFragment(), "Chats")
        adapter.addFragment(GroupsFragment(), "Groups")
        adapter.addFragment(UsersFragment(), "Users")

        binding.viewpager.adapter = adapter
        binding.viewpager.offscreenPageLimit = 3

        init();
    }

    private fun init() {

        //tabLayout.setupWithViewPager(viewPager);
        resetTab(binding.btnChats, activity)

        binding.btnUsers.setOnClickListener { view ->
            //navController.navigate(R.id.usersFragment, null, options);
            binding.viewpager.setCurrentItem(2, true)
        }
        binding.btnGroups.setOnClickListener { view ->
            //navController.navigate(R.id.chatsFragment, null, options);
            binding.viewpager.setCurrentItem(1, true)
        }
        binding.btnChats.setOnClickListener { view ->
            //navController.navigate(R.id.chatsFragment, null, options);
            binding.viewpager.setCurrentItem(0, true)
        }

        binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    resetTab(binding.btnChats, activity)
                } else if (position == 1) {
                    resetTab(binding.btnGroups, activity)
                } else if (position == 2) {
                    resetTab(binding.btnUsers, activity)
                }
            }
        })

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun resetTab(textView: TextView, activity: Activity) {
        binding.btnChats.background = null
        binding.btnChats.background = AppCompatResources.getDrawable(contexts,R.drawable.green_border)
        binding.btnChats.setTextColor(activity.resources.getColor(R.color.thim_color,activity.theme))

        binding.btnGroups.background = null
        binding.btnGroups.background = AppCompatResources.getDrawable(contexts,R.drawable.green_border)
        binding.btnGroups.setTextColor(activity.resources.getColor(R.color.thim_color,activity.theme))

        binding.btnUsers.background = null
        binding.btnUsers.background = AppCompatResources.getDrawable(contexts,R.drawable.green_border)
        binding.btnUsers.setTextColor(activity.resources.getColor(R.color.thim_color,activity.theme))


        textView.background = null
        textView.setBackgroundColor(activity.resources.getColor(R.color.thim_color,activity.theme))
        textView.setTextColor(activity.resources.getColor(R.color.white))
    }


}