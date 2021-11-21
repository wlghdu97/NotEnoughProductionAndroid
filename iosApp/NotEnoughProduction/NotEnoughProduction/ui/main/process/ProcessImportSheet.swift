//
//  ProcessImportSheet.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//

import SwiftUI
import Shared

struct ProcessImportSheet: View {
    @StateObject var viewModel: ProcessImportSwiftUIViewModel
    @Binding var showSheet: Bool
    @State private var importString = "" {
        didSet {
            viewModel.notifyTextChanged()
        }
    }

    var body: some View {
        NavigationView {
            List {
                Section {
                    Button {
                        if let clipboardString = UIPasteboard.general.string {
                            importString = clipboardString
                        }
                    } label: {
                        Label(MR.strings().txt_copy_from_clipboard.desc().localized(), systemImage: "doc.on.clipboard")
                    }
                }
                Section {
                    VStack(alignment: .leading) {
                        if !viewModel.isStringValid {
                            Text(MR.strings().txt_invalid_process_string.desc().localized())
                                .font(.caption2)
                                .foregroundColor(.red)
                                .padding(.bottom, 4)
                        }
                        TextEditor(text: $importString)
                            .lineLimit(nil)
                    }
                    .animation(.default, value: viewModel.isStringValid)
                }
            }
            .navigationBarTitle(MR.strings().title_import_process.desc().localized())
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button {
                        showSheet = false
                    } label: {
                        Text(MR.strings().btn_close.desc().localized())
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        viewModel.importProcess(importString)
                    } label: {
                        Text(MR.strings().btn_import.desc().localized())
                    }
                    .disabled(!viewModel.isStringValid)
                }
            }
        }
        .onChange(of: viewModel.dismiss) { dismiss in
            if dismiss {
                showSheet = false
            }
        }
    }
}

struct ProcessImportSheet_Previews: PreviewProvider {
    static var previews: some View {
        ProcessImportSheet(viewModel: ProcessImportSwiftUIViewModel(), showSheet: .constant(true))
    }
}
