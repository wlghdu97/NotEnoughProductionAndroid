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
    @State private var usageCount = 0

    var body: some View {
        List { [items = viewModel.usageList] in
            Section(header: Text(usageListHeaderText)) {
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
        .onChange(of: viewModel.usageList) { list in
            usageCount = list.count
        }
    }
}

extension ElementUsageList {
    fileprivate static let stringResolver = StringResolver()

    fileprivate var usageListHeaderText: String {
        ElementUsageList.stringResolver.formatString(format: MR.strings().form_tab_usages, args: usageCount)
    }
}

struct ElementUsageList_Previews: PreviewProvider {
    static var previews: some View {
        ElementUsageList(viewModel: ElementUsageListSwiftUIViewModel())
    }
}
