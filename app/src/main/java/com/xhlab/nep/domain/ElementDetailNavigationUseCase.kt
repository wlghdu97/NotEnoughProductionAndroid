package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.element.ElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailFragment.Companion.ELEMENT_ID
import com.xhlab.nep.ui.element.ElementDetailFragment.Companion.ELEMENT_TYPE
import javax.inject.Inject

class ElementDetailNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<ElementDetailNavigationUseCase.Parameters, Unit>() {

    override suspend fun execute(params: Parameters) {
        context.startActivity(Intent(context, ElementDetailActivity::class.java).apply {
            putExtra(ELEMENT_ID, params.elementId)
            putExtra(ELEMENT_TYPE, params.elementType)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameters(val elementId: Long, val elementType: Int)
}