package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.BaseUseCase
import com.xhlab.nep.ui.element.ElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailFragment
import javax.inject.Inject

class ElementDetailNavigationUseCase @Inject constructor(
    private val context: Context
) : BaseUseCase<ElementDetailNavigationUseCase.Parameters, Unit>() {

    override suspend fun execute(params: Parameters) {
        context.startActivity(Intent(context, ElementDetailActivity::class.java).apply {
            putExtras(ElementDetailFragment.getBundle(params))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameters(val elementId: Long, val elementType: Int)
}
