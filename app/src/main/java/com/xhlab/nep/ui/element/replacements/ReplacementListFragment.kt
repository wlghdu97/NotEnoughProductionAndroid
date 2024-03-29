package com.xhlab.nep.ui.element.replacements

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentReplacementListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.element.replacements.ReplacementListViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailActivity.Companion.navigateToElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.ui.main.items.RecipeElementDetailAdapter
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ReplacementListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentReplacementListBinding
    private lateinit var viewModel: ReplacementListViewModel

    private val elementAdapter by lazy { RecipeElementDetailAdapter(viewModel) }

    private lateinit var oreDictName: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReplacementListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        oreDictName = arguments?.getString(ORE_DICT_NAME) ?: getString(R.string.txt_unknown)
        viewModel.init(oreDictName)

        viewModel.replacementList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            elementAdapter.submitData(lifecycle, it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) { isLoaded ->
            elementAdapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToDetail.asLiveData().observe(this) { elementId ->
            if (resources.getBoolean(R.bool.isTablet)) {
                val parent = requireParentFragment().requireParentFragment()
                parent.childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                    .add(R.id.container, ElementDetailFragment.getFragment(elementId))
                    .addToBackStack(null)
                    .commit()
            } else {
                context?.navigateToElementDetailActivity(elementId)
            }
        }
    }

    override fun initView() {
        binding.oreDictName.text = oreDictName
        binding.replacementList.adapter = elementAdapter

        elementAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                binding.oreDictName.text = requireContext().formatString(
                    R.string.form_ore_dict_name,
                    oreDictName,
                    elementAdapter.itemCount
                )
            }
        }
    }

    companion object {
        const val ORE_DICT_NAME = "ore_dict_name"
    }
}
