package com.xhlab.nep.ui.element.replacements

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xhlab.nep.R
import com.xhlab.nep.ui.element.ElementDetailActivity.Companion.ELEMENT_ID

class ReplacementContainerFragment : Fragment() {

    private var elementId: Long = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkIntent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_replacement_container, container, false)
    }

    private fun checkIntent() {
        elementId = arguments?.getLong(ELEMENT_ID) ?: 0L
        require(elementId != 0L)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fm = requireFragmentManager()
        val fragment = fm.findFragmentByTag(ORE_DICT_LIST_TAG) ?: OreDictListFragment()
        fragment.arguments = Bundle().apply {
            putLong(ELEMENT_ID, elementId)
        }
        fm.beginTransaction()
            .replace(R.id.container, fragment, ORE_DICT_LIST_TAG)
            .commit()
    }

    companion object {
        private const val ORE_DICT_LIST_TAG = "ore_dict_list_tag"
    }
}