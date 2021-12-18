//
//  CraftingRecipeViewItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/26.
//

import SwiftUI
import Shared

struct CraftingRecipeViewItem: View {
    let recipe: ModelRecipeView
    let targetElementId: Int64
    let elementListener: ElementListener?
    let withIcon: Bool

    private var targetElement: ModelRecipeElement! {
        recipe.resultItemList.first { $0.id == targetElementId }
    }
    private var byproductList: [ModelRecipeElement] {
        recipe.resultItemList.filter { $0.id != targetElementId }
    }

    var body: some View {
        VStack(alignment: .leading) {
            Text(recipe.getTitle(targetElement))
            Text(MR.strings().txt_crafting_table.desc().localized())
                .captionText()
            Divider()
            Text(MR.strings().txt_ingredients.desc().localized())
                .captionText()
                .padding(.vertical, 4)
            ForEach(recipe.itemList, id: \.id) { item in
                RecipeViewElementItem(element: item, elementListener: elementListener, withIcon: withIcon)
                    .equatable()
            }
        }
        .listCardBackground()
        .padding(.top, 8)
    }
}

extension CraftingRecipeViewItem: Equatable {
    static func == (lhs: CraftingRecipeViewItem, rhs: CraftingRecipeViewItem) -> Bool {
        (lhs.recipe == rhs.recipe) && (lhs.targetElementId == rhs.targetElementId)
    }
}
