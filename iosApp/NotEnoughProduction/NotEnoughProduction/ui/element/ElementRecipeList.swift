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
    @State private var recipeCount = 0

    var body: some View {
        List { [items = viewModel.recipeList] in
            Section(header: Text(recipeListHeaderText)) {
                ForEach(items, id: \.machineId) { item in
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
        .onChange(of: viewModel.recipeList) { list in
            recipeCount = list.map({ Int($0.recipeCount) }).reduce(0, +)
        }
    }
}

extension ElementRecipeList {
    fileprivate static let stringResolver = StringResolver()

    fileprivate var recipeListHeaderText: String {
        ElementRecipeList.stringResolver.formatString(format: MR.strings().form_tab_recipes, args: recipeCount)
    }
}

struct ElementRecipeList_Previews: PreviewProvider {
    static var previews: some View {
        ElementRecipeList(viewModel: ElementRecipeListSwiftUIViewModel())
    }
}
