//
//  ElementRecipeList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/23.
//

import SwiftUI
import Shared

struct ElementRecipeList: View {
    @StateObject var viewModel: ElementRecipeListSwiftUIViewModel

    var body: some View {
        List { [items = viewModel.recipeList] in
            Section(header: Text(recipeListHeaderText)) {
                ForEach(items, id: \.machineId) { item in
                    NavigationLink {
                        MachineRecipeList(viewModel: viewModel.createMachineRecipeListViewModel(item.machineId))
                    } label: {
                        RecipeMachineViewItem(view: item)
                            .equatable()
                            .onAppear {
                                if items.last == item {
                                    viewModel.loadMoreItems()
                                }
                            }
                    }
                }
            }
        }
    }
}

extension ElementRecipeList {
    fileprivate var recipeListHeaderText: String {
        StringResolver.global.formatString(format: MR.strings().form_tab_recipes, args: viewModel.totalCount)
    }
}

struct ElementRecipeList_Previews: PreviewProvider {
    static var previews: some View {
        ElementRecipeList(viewModel: ElementRecipeListSwiftUIViewModel())
    }
}
