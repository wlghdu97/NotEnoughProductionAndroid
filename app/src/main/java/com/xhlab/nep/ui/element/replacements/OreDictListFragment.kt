package com.xhlab.nep.ui.element.replacements

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailActivity.Companion.ELEMENT_ID
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_ore_dict_list.*
import javax.inject.Inject

class OreDictListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: OreDictListViewModel

    private val oreDictAdapter by lazy { OreDictAdapter(viewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ore_dict_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(arguments?.getLong(ELEMENT_ID))

        viewModel.oreDictNameList.observe(this) {
            oreDictAdapter.submitList(it)
        }

        viewModel.navigateToReplacementList.observe(this) {
            switchToReplacementList(it)
        }
    }

    override fun initView() {
        ore_dict_list.adapter = oreDictAdapter
    }

    private fun switchToReplacementList(oreDictName: String) {
        val fm = requireFragmentManager()
        val fragment = fm.findFragmentByTag(REPLACEMENT_LIST_TAG) ?: ReplacementListFragment()
        fragment.arguments = Bundle().apply {
            putString(ReplacementListFragment.ORE_DICT_NAME, oreDictName)
        }
        fm.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, fragment, REPLACEMENT_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val REPLACEMENT_LIST_TAG = "replacement_list_tag"
    }
}