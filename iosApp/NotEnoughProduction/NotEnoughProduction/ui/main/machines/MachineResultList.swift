//
//  MachineResultList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/26.
//

import SwiftUI
import Shared

struct MachineResultList: View {
    @StateObject var viewModel: MachineResultSwiftUIViewModel
    @State private var searchTerm = ""

    var body: some View {
        List { [items = viewModel.resultList] in
            Section(header: Text(MR.strings().tab_usages.desc().localized())) {
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
        .animation(.default, value: viewModel.resultList)
        .navigationTitle(Text(viewModel.machine?.name ?? MR.strings().txt_unknown.desc().localized()))
        .searchable(text: $searchTerm)
        .onChange(of: searchTerm) { term in
            viewModel.searchResults(term)
        }
    }
}

struct MachineResultList_Previews: PreviewProvider {
    static var previews: some View {
        MachineResultList(viewModel: MachineResultSwiftUIViewModel())
    }
}
