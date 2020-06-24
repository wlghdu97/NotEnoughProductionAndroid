package com.xhlab.nep.model.process.view

import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.recipes.view.RecipeView

class ProcessView(
    id: String,
    name: String,
    rootRecipe: RecipeView,
    targetOutput: ElementView
) : Process<RecipeView>(id, name, rootRecipe, targetOutput)