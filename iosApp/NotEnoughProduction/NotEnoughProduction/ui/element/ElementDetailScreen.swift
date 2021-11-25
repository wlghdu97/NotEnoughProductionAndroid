//
//  ElementDetailScreen.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//

import SwiftUI
import Shared

struct ElementDetailScreen: View {
    @StateObject var viewModel: ElementDetailSwiftUIViewModel

    var body: some View {
        VStack {
            if let element = viewModel.element {
                HStack {
                    VStack(alignment: .leading) {
                        Text(element.localizedName.unnamedIfEmpty())
                        if let unlocalizedName = element.unlocalizedName, !unlocalizedName.isEmpty {
                            Text(unlocalizedName)
                                .captionText()
                        }
                    }
                    Spacer()
                    Text(element.elementTypeText)
                        .captionText()
                }
                .listCardBackground()
                TabView {
                    if element.type == ModelElement.companion.ORE_CHAIN {
                        ElementOreDictList(viewModel: viewModel.createOreDictListViewModel(element.id))
                    } else {
                        ElementRecipeList(viewModel: viewModel.createRecipeListViewModel(element.id))
                    }
                    ElementUsageList(viewModel: viewModel.createUsageListViewModel(element.id))
                }
                .tabViewStyle(.page)
            }
        }
        .animation(.default, value: viewModel.element)
        .navigationTitle(MR.strings().title_element_details.desc().localized())
    }
}

struct ElementDetailScreen_Previews: PreviewProvider {
    static var previews: some View {
        ElementDetailScreen(viewModel: ElementDetailSwiftUIViewModel())
    }
}
