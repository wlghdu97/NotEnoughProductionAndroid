//
//  ElementReplacementList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/24.
//

import SwiftUI
import Shared

struct ElementReplacementList: View {
    @StateObject var viewModel: ElementReplacementListSwiftUIViewModel

    var body: some View {
        Group { [items = viewModel.replacementList] in
            ForEach(items, id: \.id) { item in
                NavigationLink {
                    ElementDetailScreen(viewModel: viewModel.createElementDetailViewModel(item.id))
                } label: {
                    RecipeElementItem(element: item)
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

struct ElementReplacementList_Previews: PreviewProvider {
    static var previews: some View {
        ElementReplacementList(viewModel: ElementReplacementListSwiftUIViewModel())
    }
}
