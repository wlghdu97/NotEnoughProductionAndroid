package com.xhlab.nep.ui.util

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearItemSpacingDecorator(@Px private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val lm = parent.layoutManager
        if (lm is LinearLayoutManager) {
            when (lm.orientation) {
                LinearLayoutManager.HORIZONTAL -> {
                    when (lm.stackFromEnd) {
                        true -> outRect.left = space
                        false -> outRect.right = space
                    }
                }
                LinearLayoutManager.VERTICAL -> {
                    when (lm.stackFromEnd) {
                        true -> outRect.top = space
                        false -> outRect.bottom = space
                    }
                }
            }
        }
    }
}