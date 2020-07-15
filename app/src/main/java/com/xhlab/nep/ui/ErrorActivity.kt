package com.xhlab.nep.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xhlab.nep.BuildConfig
import com.xhlab.nep.R
import kotlinx.android.synthetic.main.activity_error.*

class ErrorActivity : AppCompatActivity() {

    private lateinit var message: TextView
    private lateinit var restartBtn: Button

    companion object {
        const val ERROR_EXTRA_INTENT = "error_extra_intent"
        const val ERROR_EXTRA_TEXT = "error_extra_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        message = activity_error_msg
        if (BuildConfig.DEBUG) {
            val msg = intent.getStringExtra(ERROR_EXTRA_TEXT)
            message.text = msg
        }

        val intent = intent.getParcelableExtra<Intent>(ERROR_EXTRA_INTENT)
        restartBtn = activity_relaunch_btn
        restartBtn.setOnClickListener {
            with (intent) {
                startActivity(this)
                finish()
            }
        }
    }
}