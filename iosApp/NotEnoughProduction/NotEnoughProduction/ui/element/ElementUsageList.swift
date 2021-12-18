//
//  ElementUsageList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/23.
//

import SwiftUI
import Shared

struct ElementUsageList: View {
    @StateObject var viewModel: ElementUsageListSwiftUIViewModel

    var body: some View {
        List { [items = viewModel.usageList] in
            Section(header: Text(usageListHeaderText)) {
                ForEach(items, id: \.id) { item in
                    NavigationLink {
                        ElementDetailScreen(viewModel: viewModel.createElementDetailViewModel(item.id))
                    } label: {
                        RecipeElementItem(element: item, withIcon: viewModel.isIconLoaded)
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

extension ElementUsageList {
    fileprivate var usageListHeaderText: String {
        StringResolver.global.formatString(format: MR.strings().form_tab_usages, args: viewModel.totalCount)
    }
}

struct ElementUsageList_Previews: PreviewProvider {
    static var previews: some View {
        ElementUsageList(viewModel: ElementUsageListSwiftUIViewModel())
    }
}
