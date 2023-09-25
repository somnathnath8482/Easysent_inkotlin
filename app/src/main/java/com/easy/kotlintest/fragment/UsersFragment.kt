package com.easy.kotlintest.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import com.easy.kotlintest.Networking.Interface.AllInterFace
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.Room.Users.Users
import com.easy.kotlintest.activity.Message.MessageActivity
import com.easy.kotlintest.adapter.UserAdapter
import com.easy.kotlintest.databinding.FragmentUsersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class UsersFragment : Fragment(), CoroutineScope {

    private lateinit var binding: FragmentUsersBinding
    lateinit var userVewModel: UserVewModel
    var handler = Handler(Looper.getMainLooper())


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userVewModel = ViewModelProviders.of(this@UsersFragment)[UserVewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.setHasFixedSize(true)
        val adapter = UserAdapter(object : DiffUtil.ItemCallback<Users>() {
            override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
                return oldItem.email == newItem.email
            }

            override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
                return (oldItem.rid == newItem.rid
                        && oldItem.id == newItem.id
                        && oldItem.fstatus == newItem.fstatus
                        && oldItem.name == newItem.name)
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
        userVewModel.getAll {
            handler.post(Runnable {

                it.observe(viewLifecycleOwner) {

                    launch {
                        adapter.submitData(it)
                    }


                }

            })
        };

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                userVewModel.search(
                    charSequence.toString().trim()
                ) {
                    handler.post(Runnable {

                        it.observe(viewLifecycleOwner) {

                            launch {
                                adapter.submitData(it)
                            }


                        }

                    })

                };
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


}