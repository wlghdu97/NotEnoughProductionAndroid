//
//  MachineRecipeViewItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import SwiftUI
import Shared

struct MachineRecipeViewItem: View {
    let recipe: ModelMachineRecipeView
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
            Text(recipe.machienNameText)
                .captionText()
            Text(recipe.detailsText)
                .padding(.vertical)
            Divider()
            Text(MR.strings().txt_ingredients.desc().localized())
                .captionText()
                .padding(.vertical, 4)
            ForEach(recipe.itemList, id: \.id) { item in
                RecipeViewElementItem(element: item, elementListener: elementListener, withIcon: withIcon)
                    .equatable()
            }
            if !byproductList.isEmpty {
                Divider()
                Text(MR.strings().txt_byproducts.desc().localized())
                    .captionText()
                    .padding(.vertical, 4)
                ForEach(byproductList, id: \.id) { item in
                    RecipeViewElementItem(element: item, elementListener: elementListener, withIcon: withIcon)
                }
            }
        }
        .listCardBackground()
        .padding(.top, 8)
    }
}

extension MachineRecipeViewItem: Equatable {
    static func == (lhs: MachineRecipeViewItem, rhs: MachineRecipeViewItem) -> Bool {
        (lhs.recipe == rhs.recipe) && (lhs.targetElementId == rhs.targetElementId)
    }
}
