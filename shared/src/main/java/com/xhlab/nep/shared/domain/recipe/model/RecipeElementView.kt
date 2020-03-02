package com.xhlab.nep.shared.domain.recipe.model

import com.xhlab.nep.shared.domain.item.model.ElementView

abstract class RecipeElementView : ElementView() {
    abstract val amount: Int
}