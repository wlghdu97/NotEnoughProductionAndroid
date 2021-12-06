//
//  ItemBrowser.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import SwiftUI
import Shared

struct ItemBrowser: View {
    @StateObject var viewModel: ItemBrowserSwiftUIViewModel
    @State private var searchTerm = ""

    var body: some View {
        Group {
            if viewModel.isDBLoaded {
                List { [items = viewModel.itemList] in
                    Section(header: Text(StringResolver.global.formatString(format: MR.strings().form_matched_total, args: viewModel.matchedCount))) {
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
                .listStyle(.grouped)
            } else {
                Text(MR.strings().txt_db_not_loaded.desc().localized())
            }
        }
        .animation(.default, value: viewModel.isDBLoaded)
        .navigationTitle(MR.strings().menu_item_browser.desc().localized())
        .navigationBarTitleDisplayMode(.inline)
        .searchable(text: $searchTerm, placement: .navigationBarDrawer(displayMode: .always))
        .onChange(of: searchTerm) { term in
            viewModel.search(term)
        }
    }
}

struct ItemBrowser_Previews: PreviewProvider {
    static var previews: some View {
        ItemBrowser(viewModel: ItemBrowserSwiftUIViewModel())
    }
}
