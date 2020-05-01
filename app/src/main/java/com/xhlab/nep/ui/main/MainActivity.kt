package com.xhlab.nep.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.service.ParseRecipeService.Companion.JSON_URI
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.machines.MachineBrowserFragment
import com.xhlab.nep.ui.main.settings.SettingsFragment
import com.xhlab.nep.ui.parser.JsonParseDialog
import com.xhlab.nep.ui.parser.JsonParseDialog.Companion.SHOW_JSON_PARSER_DIALOG
import com.xhlab.nep.util.updateGlobalTheme
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.longToast
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    private lateinit var pagerAdapter: MainViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        updateGlobalTheme(viewModel.currentTheme)
    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        pagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        view_pager.adapter = pagerAdapter

        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                bottom_navigation.menu.getItem(position).isChecked = true
            }
        })

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_browser -> {
                    view_pager.currentItem = 0
                    true
                }
                R.id.menu_machine_browser -> {
                    view_pager.currentItem = 1
                    true
                }
                R.id.menu_settings -> {
                    view_pager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_open_json) {
            browseJsonFiles()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_JSON_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = data?.data
                    if (uri != null) {
                        showParseNoticeDialog(uri)
                    } else {
                        longToast(R.string.txt_invalid_uri)
                    }
                }
                Activity.RESULT_CANCELED -> Unit // do nothing
                else -> {
                    longToast(R.string.error_open_json_failed)
                }
            }
        }
    }

    private fun browseJsonFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = when (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                true -> "application/json"
                false -> "application/octet-stream"
            }
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivityForResult(intent, READ_JSON_CODE)
    }

    private fun showParseNoticeDialog(fileUri: Uri) {

        fun showJsonParseDialog(fileUri: Uri) {
            JsonParseDialog().apply {
                arguments = Bundle().apply { putParcelable(JSON_URI, fileUri) }
            }.show(supportFragmentManager, SHOW_JSON_PARSER_DIALOG)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.title_parse_json)
            .setMessage(R.string.txt_parse_json)
            .setPositiveButton(R.string.btn_ok) { _, _ ->
                showJsonParseDialog(fileUri)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private class MainViewPagerAdapter(
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val itemBrowserFragment by lazy { ItemBrowserFragment() }
        private val machineBrowserFragment by lazy { MachineBrowserFragment() }
        private val settingsFragment by lazy { SettingsFragment() }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> itemBrowserFragment
                1 -> machineBrowserFragment
                2 -> settingsFragment
                else -> throw IllegalArgumentException("invalid position.")
            }
        }

        override fun getCount() = 3
    }

    companion object {
        private const val READ_JSON_CODE = 100
    }
}
