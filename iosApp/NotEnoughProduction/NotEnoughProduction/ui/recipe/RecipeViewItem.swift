//
//  RecipeViewItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import SwiftUI
import Shared
import Introspect

struct RecipeViewItem: View {
    let recipe: ModelRecipeView
    let targetElement: Int64
    let elementListener: ElementListener?
    let withIcon: Bool

    var body: some View {
        if let machineRecipe = recipe as? ModelMachineRecipeView {
            MachineRecipeViewItem(recipe: machineRecipe, targetElementId: targetElement, elementListener: elementListener, withIcon: withIcon)
        } else {
            CraftingRecipeViewItem(recipe: recipe, targetElementId: targetElement, elementListener: elementListener, withIcon: withIcon)
        }
    }
}

extension RecipeViewItem: Equatable {
    static func == (lhs: RecipeViewItem, rhs: RecipeViewItem) -> Bool {
        (lhs.recipe.recipeId == rhs.recipe.recipeId) && (lhs.targetElement == rhs.targetElement)
    }
}
