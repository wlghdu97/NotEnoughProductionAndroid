package com.xhlab.nep.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.service.ParseRecipeService.Companion.JSON_URI
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.parser.JsonParseDialog
import com.xhlab.nep.ui.parser.JsonParseDialog.Companion.SHOW_JSON_PARSER_DIALOG
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.longToast
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), ViewInit {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun initViewModel() {
    }

    override fun initView() {
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
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                if (uri != null) {
                    showParseNoticeDialog(uri)
                } else {
                    longToast(R.string.txt_invalid_uri)
                }
            } else {
                longToast(R.string.error_open_json_failed)
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

    companion object {
        private const val READ_JSON_CODE = 100
    }
}
