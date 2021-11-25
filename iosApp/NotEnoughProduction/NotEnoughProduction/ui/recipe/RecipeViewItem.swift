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

    var body: some View {
        if let machineRecipe = recipe as? ModelMachineRecipeView {
            MachineRecipeViewItem(recipe: machineRecipe, targetElementId: targetElement, elementListener: elementListener)
        } else {
            CraftingRecipeViewItem(recipe: recipe, targetElementId: targetElement, elementListener: elementListener)
        }
    }
}

extension RecipeViewItem: Equatable {
    static func == (lhs: RecipeViewItem, rhs: RecipeViewItem) -> Bool {
        (lhs.recipe == rhs.recipe) && (lhs.targetElement == rhs.targetElement)
    }
}
