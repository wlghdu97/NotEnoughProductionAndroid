//
//  ProcessItemBrowser.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/07.
//

import SwiftUI
import Shared

struct ProcessItemBrowser: View {
    @StateObject var viewModel: ProcessItemBrowserSwiftUIViewModel
    @Binding var showItemBrowser: Bool

    var body: some View {
        ItemBrowser(viewModel: viewModel.createItemBrowserViewModel()) { _, item in
            ElementRecipeList(viewModel: viewModel.createRecipeListViewModel(item.id)) { _, recipeItem in
                ProcessMachineRecipeList(viewModel: viewModel.createMachineRecipeListViewModel(item.id, machineId: recipeItem.machineId)) { recipe, keyElement in
                    viewModel.submit(recipe, keyElement: keyElement)
                }
                .onAppear {
                    viewModel.onClick(machineId: recipeItem.machineId)
                }
                .onChange(of: viewModel.dismiss) { dismiss in
                    if dismiss {
                        showItemBrowser = false
                    }
                }
            }
            .onAppear {
                viewModel.onClick(elementId: item.id)
            }
        }
    }
}

struct ProcessItemBrowser_Previews: PreviewProvider {
    static var previews: some View {
        ProcessItemBrowser(viewModel: ProcessItemBrowserSwiftUIViewModel(), showItemBrowser: .constant(true))
    }
}
