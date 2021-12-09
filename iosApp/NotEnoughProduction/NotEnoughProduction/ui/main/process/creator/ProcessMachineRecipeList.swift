//
//  ProcessMachineRecipeList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/09.
//

import SwiftUI
import Shared

struct ProcessMachineRecipeList: View {
    @StateObject var viewModel: MachineRecipeListSwiftUIViewModel
    let onClick: ((ModelRecipe, ModelRecipeElement) -> Void)?

    var body: some View {
        ScrollView {
            LazyVStack { [items = viewModel.recipeList] in
                ForEach(items, id: \.recipeId) { recipe in
                    Button {
                        if let keyElement = recipe.resultItemList.first(where: { $0.id == viewModel.targetElementId }) {
                            onClick?(recipe, keyElement)
                        } else {
                            debugPrint("Could not found target element of this recipe.")
                        }
                    } label: {
                        RecipeViewItem(recipe: recipe, targetElement: viewModel.targetElementId, elementListener: nil, withIcon: viewModel.isIconLoaded)
                            .equatable()
                            .contentShape(Rectangle())
                    }
                    .buttonStyle(.plain)
                    .onAppear {
                        if items.last == recipe {
                            viewModel.loadMoreRecipes()
                        }
                    }
                }
            }
        }
        .navigationTitle(MR.strings().title_recipe_list.desc().localized())
    }
}

struct ProcessMachineRecipeList_Previews: PreviewProvider {
    static var previews: some View {
        ProcessMachineRecipeList(viewModel: MachineRecipeListSwiftUIViewModel(), onClick: nil)
    }
}
