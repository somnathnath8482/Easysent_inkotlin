package com.easy.kotlintest.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.easy.kotlintest.BuildConfig
import com.easy.kotlintest.Helper.ImageGetter
import com.easy.kotlintest.Helper.PrefFile.PrefUtill
import com.easy.kotlintest.Networking.Helper.Constants
import com.easy.kotlintest.Networking.Helper.MethodClass
import com.easy.kotlintest.R
import com.easy.kotlintest.Room.Users.UserVewModel
import com.easy.kotlintest.databinding.ActivityMainBinding
import com.easy.kotlintest.databinding.LeftMenuBinding
import com.easy.kotlintest.databinding.MainToolbarBinding
import kotlinx.coroutines.Runnable
import java.io.File

/**
 * Created by Somnath nath on 11,September,2023
 * Artix Development,
 * India.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbarBinding: MainToolbarBinding
    private lateinit var leftMenuBinding: LeftMenuBinding
    private lateinit var uuserviewModel: UserVewModel
    private lateinit var context: Context
    private var handler = Handler(Looper.getMainLooper())

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavController
        lateinit var options: NavOptions


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this;
        toolbarBinding = MainToolbarBinding.bind(binding.toolbar.root)
        leftMenuBinding = LeftMenuBinding.bind(binding.navigationView.getHeaderView(0))
        uuserviewModel = UserVewModel(application)

        init()
        DrawaerClick()
    }

    private fun init() {
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.slide_in)
            .setExitAnim(R.anim.fade_oout) /*.setPopEnterAnim(R.anim.fade_i_n)
               / * .setPopExitAnim(R.anim.slide_oout)*/

        options = navBuilder
            .setLaunchSingleTop(true).build()
        navController = navHostFragment!!.navController
        leftMenuBinding.version.setText(BuildConfig.VERSION_NAME)

        toolbarBinding.menu.setOnClickListener { view ->
            binding.drawer.openDrawer(
                GravityCompat.START
            )
        }


        uuserviewModel.selectUserLive(
            PrefUtill.getUser()?.user?.id
        ) { item ->
            handler.post(Runnable {
                item?.observe(this@MainActivity)
                { user ->
                    if (user != null) {
                        toolbarBinding.name.text = user?.name
                        toolbarBinding.email.text = user?.email

                        if (user?.profilePic != null && !user?.profilePic.equals("null")) {
                            val url: String =
                                Constants.BASE_URL + "profile_image/" + user.profilePic
                            val file = File(Constants.CATCH_DIR + "/" + user.profilePic)
                            if (file.exists()) {
                                Thread {
                                    val imageGetter = ImageGetter(toolbarBinding.profileImage)
                                    imageGetter.execute(file)
                                }.start()
                            } else {
                                Glide.with(context).load(url).thumbnail(0.05f)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .addListener(object : RequestListener<Drawable?> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any,
                                            target: Target<Drawable?>,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            toolbarBinding.profileImage.setImageDrawable(
                                                AppCompatResources.getDrawable(
                                                    context, MethodClass.getResId(
                                                        user.name, Drawable::class.java
                                                    )
                                                )
                                            )
                                            return true
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any,
                                            target: Target<Drawable?>,
                                            dataSource: DataSource,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            toolbarBinding.profileImage.setImageDrawable(resource)
                                            MethodClass.CashImage(
                                                Constants.BASE_URL + "profile_image/" + user.profilePic,
                                                user.profilePic,
                                                resource
                                            )
                                            return false
                                        }
                                    }).into(toolbarBinding.profileImage)
                            }
                        } else {
                            toolbarBinding.profileImage.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    context, MethodClass.getResId(
                                        user?.name, Drawable::class.java
                                    )
                                )
                            )
                        }

                    }

                }
            })
        }

    }


    private fun DrawaerClick() {
        leftMenuBinding.closeDrawer.setOnClickListener { view ->
            binding.drawer.closeDrawer(
                GravityCompat.START
            )
        }
        leftMenuBinding.layLogout.setOnClickListener { view ->
            binding.drawer.closeDrawer(GravityCompat.START)
            MethodClass.logout(
                this@MainActivity,
                PrefUtill.getUser()?.user?.id
            )
        }
        leftMenuBinding.layEditProfile.setOnClickListener { view ->
            binding.drawer.closeDrawer(GravityCompat.START)
            startActivity(Intent(this@MainActivity, EditProfilrActivity::class.java))
        }
        leftMenuBinding.layChangePassword.setOnClickListener { view ->
            binding.drawer.closeDrawer(GravityCompat.START)
            // navController.navigate(R.id.chengePasswordFragment, null, options)
        }
        leftMenuBinding.laySettings.setOnClickListener { view ->
            binding.drawer.closeDrawer(GravityCompat.START)
            //  navController.navigate(R.id.settengsFragment, null, options)
        }
        leftMenuBinding.layClip.setOnClickListener { view ->
            binding.drawer.closeDrawer(GravityCompat.START)
            // navController.navigate(R.id.clipsFragment, null, options)
        }
    }

    override fun onResume() {
        super.onResume()
        MethodClass.isAllowed(this@MainActivity)
        Create()
    }

    private fun Create() {
        val folder_main = "EasySent"
        val folder_secondary = "EasySent/Cash"
        val dir = File(this.filesDir, folder_main)
        val dir2 =
            File(Environment.getExternalStorageDirectory().toString() + "/Download/", folder_main)
        val dir3 = File(this.filesDir, folder_secondary)
        if (!dir.exists()) {
            val `is` = dir.mkdirs()
            if (`is`) {
                Constants.CATCH_DIR = dir.absolutePath.toString()
            }
            Log.d("TAG", "Create Folder: $`is`")
        } else {
            Constants.CATCH_DIR = dir.absolutePath.toString()
        }
        if (!dir3.exists()) {
            val `is` = dir3.mkdirs()
            if (`is`) {
                Constants.CATCH_DIR3 = dir3.absolutePath.toString()
            }
            Log.d("TAG", "Create Folder: $`is`")
        } else {
            if (dir3.isDirectory) {
                val children = dir3.list()
                for (i in children.indices) {
                    File(dir3, children[i]).delete()
                }
            }
            val `is` = dir3.mkdirs()
            if (`is`) {
                Constants.CATCH_DIR3 = dir3.absolutePath.toString()
            }
            Log.d("TAG", "Create Folder: $`is`")
        }
        if (!dir2.exists()) {
            val `is` = dir2.mkdirs()
            if (`is`) {
                Constants.CATCH_DIR2 = dir2.absolutePath.toString()
            }
            Log.d("TAG", "Create Folder: $`is`")
        } else {
            Constants.CATCH_DIR2 = dir2.absolutePath.toString()
        }
    }
}