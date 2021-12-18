//
//  ElementOreDictList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/24.
//

import SwiftUI
import Shared

struct ElementOreDictList: View {
    @StateObject var viewModel: ElementOreDictListSwiftUIViewModel

    var body: some View {
        List { [items = viewModel.oreDictNameList] in
            Section(header: Text(MR.strings().txt_ore_dict_chain.desc().localized())) {
                ForEach(items, id: \.self) { name in
                    DisclosureGroup(name) {
                        ElementReplacementList(viewModel: viewModel.createReplacementListViewModel(name))
                    }
                    .onAppear {
                        if items.last == name {
                            viewModel.loadMoreNames()
                        }
                    }
                }
            }
        }
    }
}

struct ElementOreDictList_Previews: PreviewProvider {
    static var previews: some View {
        ElementOreDictList(viewModel: ElementOreDictListSwiftUIViewModel())
    }
}
