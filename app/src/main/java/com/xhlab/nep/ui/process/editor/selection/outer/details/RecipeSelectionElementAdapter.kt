package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.view.View
import androidx.core.view.isGone
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.ui.adapters.RecipeElementAdapter
import com.xhlab.nep.ui.adapters.RecipeElementViewHolder
import com.xhlab.nep.util.setIcon

class RecipeSelectionElementAdapter : RecipeElementAdapter() {

    override fun createViewHolder(itemView: View): RecipeElementViewHolder {
        return PlainRecipeElementViewHolder(itemView)
    }

    inner class PlainRecipeElementViewHolder(itemView: View) : RecipeElementViewHolder(itemView) {

        init {
            with(itemView) {
                background = null
                isClickable = false
                isFocusable = false
            }
        }

        override fun bindNotNull(model: RecipeElement) {
            super.bindNotNull(model)
            icon.isGone = !isIconVisible
            if (isIconVisible) {
                icon.setIcon(model.unlocalizedName)
            }
        }
    }
}
