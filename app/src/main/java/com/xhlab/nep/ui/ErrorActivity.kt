package com.xhlab.nep.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xhlab.nep.BuildConfig
import com.xhlab.nep.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityErrorBinding

    companion object {
        const val ERROR_EXTRA_INTENT = "error_extra_intent"
        const val ERROR_EXTRA_TEXT = "error_extra_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            val msg = intent.getStringExtra(ERROR_EXTRA_TEXT)
            binding.activityErrorMsg.text = msg
        }

        val intent = intent.getParcelableExtra<Intent>(ERROR_EXTRA_INTENT)
        binding.activityRelaunchBtn.setOnClickListener {
            with(intent) {
                startActivity(this)
                finish()
            }
        }
    }
}
