//
//  MachineRecipeList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import SwiftUI
import Shared

struct MachineRecipeList: View {
    @StateObject var viewModel: MachineRecipeListSwiftUIViewModel

    var body: some View {
        ScrollView {
            NavigationLink(destination: ElementDetailScreen(viewModel: viewModel.createElementDetailViewModel(viewModel.navigationElementId)),
                           isActive: $viewModel.navigateToElementDetail) {
                EmptyView()
            }
            LazyVStack { [items = viewModel.recipeList] in
                ForEach(items, id: \.recipeId) { recipe in
                    RecipeViewItem(recipe: recipe, targetElement: viewModel.targetElementId, elementListener: viewModel, withIcon: viewModel.isIconLoaded)
                        .equatable()
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

struct MachineRecipeList_Previews: PreviewProvider {
    static var previews: some View {
        MachineRecipeList(viewModel: MachineRecipeListSwiftUIViewModel())
    }
}
