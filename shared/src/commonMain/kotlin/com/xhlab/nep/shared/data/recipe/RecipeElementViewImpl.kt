package com.xhlab.nep.shared.data.recipe

import com.xhlab.nep.model.recipes.view.RecipeElementView

internal data class RecipeElementViewImpl(
    override val id: Long,
    override val localizedName: String,
    override val unlocalizedName: String,
    override val type: Int,
    override val metaData: String?,
    override val amount: Int
) : RecipeElementView()
