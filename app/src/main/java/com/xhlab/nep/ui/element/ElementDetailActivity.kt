package com.xhlab.nep.ui.element

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_element_detail.*
import javax.inject.Inject

class ElementDetailActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ElementDetailViewModel

    private var elementId: Long = 0L
    private var elementType: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIntent()
        initViewModel()
        initView()
    }

    private fun checkIntent() {
        elementId = intent.getLongExtra(ELEMENT_ID, 0L)
        elementType = intent.getIntExtra(ELEMENT_TYPE, -1)
        require(elementId != 0L)
        require(elementType != -1)
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(elementId)

        viewModel.element.observeNotNull(this) { element ->
            supportActionBar?.let {
                it.title = element.localizedName
                it.subtitle = element.unlocalizedName
            }
        }
    }

    override fun initView() {
        setContentView(R.layout.activity_element_detail)
        setSupportActionBar(toolbar)

        val firstTab = tab_layout.getTabAt(0)
        if (firstTab != null) {
            firstTab.text = getString(when (elementType) {
                ORE_CHAIN -> R.string.tab_replacements
                else -> R.string.tab_recipes
            })
        }
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val ELEMENT_TYPE = "element_type"

        fun navigateToElementDetail(context: Context, elementId: Long, elementType: Int) {
            val intent = Intent(context, ElementDetailActivity::class.java).apply {
                putExtra(ELEMENT_ID, elementId)
                putExtra(ELEMENT_TYPE, elementType)
            }
            context.startActivity(intent)
        }
    }
}