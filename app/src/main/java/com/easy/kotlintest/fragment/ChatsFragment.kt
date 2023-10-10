package com.easy.kotlintest.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.Room.Thread.Active_Thread
import com.easy.kotlintest.Room.Thread.Thread_ViewModel
import com.easy.kotlintest.Room.Users.Users
import com.easy.kotlintest.activity.Message.MessageActivity
import com.easy.kotlintest.adapter.ThreadAdapter
import com.easy.kotlintest.adapter.UserAdapter
import com.easy.kotlintest.databinding.FragmentChatsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ChatsFragment : Fragment(), CoroutineScope {

    private lateinit var threadViewModel: Thread_ViewModel
    private lateinit var binding: FragmentChatsBinding
    var handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        threadViewModel = ViewModelProviders.of(this@ChatsFragment)[Thread_ViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.setHasFixedSize(true)
        val adapter = ThreadAdapter(object : DiffUtil.ItemCallback<Active_Thread>() {
            override fun areItemsTheSame(oldItem: Active_Thread, newItem: Active_Thread): Boolean {
                return oldItem.email == newItem.email
            }

            override fun areContentsTheSame(oldItem: Active_Thread, newItem: Active_Thread): Boolean {
                return (oldItem.rid == newItem.rid
                        && oldItem.id == newItem.id
                        && oldItem.fstatus == newItem.fstatus
                        && oldItem.profilePic == newItem.profilePic
                        && oldItem.last_message == newItem.last_message)
            }
        }, Dispatchers.Main, object : AllInterFace() {
            override fun IsClicked(s: String?) {
                super.IsClicked(s)
                val intent = Intent(activity, MessageActivity::class.java)
                intent.putExtra("reciver", s)
                activity!!.startActivity(intent)
            }
        })
        binding.recycler.adapter = adapter
        threadViewModel.getActiveThreds(PrefUtill.getUser()?.user?.id) {
            handler.post {

                it.observe(viewLifecycleOwner) {

                    launch {
                        adapter.submitData(it)
                    }


                }

            }
        };

    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


}