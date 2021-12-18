//
//  ProcessRenameSheet.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import SwiftUI
import Shared

struct ProcessRenameSheet: View {
    @StateObject var viewModel: ProcessRenameSwiftUIViewModel
    @Binding var showSheet: Bool
    @State private var isFocused = true

    var body: some View {
        NavigationView {
            List {
                Section(header: Text(MR.strings().hint_name.desc().localized())) {
                    TextFieldContainer(MR.strings().hint_name.desc().localized(), text: $viewModel.name, isFocused: $isFocused)
                }
            }
            .navigationBarTitle(MR.strings().title_rename_process.desc().localized())
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
                        viewModel.renameProcess()
                    } label: {
                        Text(MR.strings().btn_rename.desc().localized())
                    }
                    .disabled(!viewModel.isNameValid)
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

struct ProcessRenameSheet_Previews: PreviewProvider {
    static var previews: some View {
        ProcessRenameSheet(viewModel: ProcessRenameSwiftUIViewModel(), showSheet: .constant(true))
    }
}
