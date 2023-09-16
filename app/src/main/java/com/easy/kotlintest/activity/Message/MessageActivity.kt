package com.easy.kotlintest.activity.Message

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.easy.kotlintest.Helper.FileHandel.PickFile
import com.easy.kotlintest.databinding.ActivityMessageBinding
import com.easy.kotlintest.databinding.MainToolbarBinding

class MessageActivity : AppCompatActivity() {
   private lateinit var mainToolbarBinding: MainToolbarBinding
   private lateinit var binding: ActivityMessageBinding
   private lateinit var pickFile: PickFile
   private var handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickFile = PickFile(this, this,handler)

        mainToolbarBinding = MainToolbarBinding.bind(binding.toolbar.root)
        mainToolbarBinding.back.setVisibility(View.VISIBLE)
        mainToolbarBinding.menu.setVisibility(View.GONE)
        mainToolbarBinding.menuMessage.setVisibility(View.VISIBLE)



    }
}