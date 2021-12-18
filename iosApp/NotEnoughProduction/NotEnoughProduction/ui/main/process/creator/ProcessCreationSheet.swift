//
//  ProcessCreationSheet.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/06.
//

import SwiftUI
import Shared

struct ProcessCreationSheet: View {
    @StateObject var viewModel: ProcessCreationSwiftUIViewModel
    @Binding var showSheet: Bool
    @State private var showItemBrowser = false

    var body: some View {
        NavigationView {
            List {
                Section(header: Text(MR.strings().hint_name.desc().localized())) {
                    VStack(alignment: .leading) {
                        TextField(MR.strings().hint_name.desc().localized(), text: $viewModel.processName)
                        if !viewModel.isNameValid {
                            Text(MR.strings().txt_name_empty.desc().localized())
                                .font(.caption2)
                                .foregroundColor(.red)
                                .padding(.bottom, 4)
                        }
                    }
                    .animation(.default, value: viewModel.isNameValid)
                }
                Section {
                    NavigationLink(isActive: $showItemBrowser) {
                        ProcessItemBrowser(viewModel: viewModel.createItemBrowserViewModel(), showItemBrowser: $showItemBrowser)
                    } label: {
                        if let pair = viewModel.recipePair {
                            let recipe = pair.recipe
                            let element = pair.element
                            Text(StringResolver.global.formatString(format: MR.strings().form_recipe_details, args: recipe.machienNameText, element.amount, element.localizedNameText))
                        } else {
                            Text(MR.strings().btn_select_target_recipe.desc().localized())
                        }
                    }
                }
            }
            .navigationBarTitle(MR.strings().title_create_process.desc().localized())
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        viewModel.dismiss = true
                    } label: {
                        Text(MR.strings().btn_cancel.desc().localized())
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        viewModel.createProcess()
                    } label: {
                        Text(MR.strings().btn_create.desc().localized())
                    }
                }
            }
        }
        .snackBar(isPresented: $viewModel.isSnackBarPresented, message: viewModel.snackBarMessage)
        .onChange(of: viewModel.dismiss) { dismiss in
            if dismiss {
                showSheet = false
            }
        }
    }
}

struct ProcessCreationSheet_Previews: PreviewProvider {
    static var previews: some View {
        ProcessCreationSheet(viewModel: ProcessCreationSwiftUIViewModel(), showSheet: .constant(true))
    }
}
