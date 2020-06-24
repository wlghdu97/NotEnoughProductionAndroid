package com.xhlab.nep.model.recipes.view

import com.xhlab.nep.model.ElementView

abstract class RecipeElementView : ElementView() {
    abstract override val amount: Int
}