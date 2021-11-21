//
//  ProcessExportSheet.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//

import SwiftUI
import Shared

struct ProcessExportSheet: View {
    let exportString: String
    @Binding var showSheet: Bool
    @State private var showCopyMessage = false

    var body: some View {
        NavigationView {
            List {
                Section(header: Text(MR.strings().hint_exported_string.desc().localized())) {
                    Text(exportString)
                        .lineLimit(nil)
                        .textSelection(.enabled)
                }
            }
            .navigationBarTitle(MR.strings().title_export_result.desc().localized())
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
                        UIPasteboard.general.string = exportString
                        showCopyMessage = true
                    } label: {
                        Image(systemName: "doc.on.doc")
                    }
                }
            }
        }
        .snackBar(isPresented: $showCopyMessage, message: MR.strings().txt_copied_to_clipboard.desc().localized())
    }
}

struct ProcessExportSheet_Previews: PreviewProvider {
    static var previews: some View {
        ProcessExportSheet(exportString: "", showSheet: .constant(true))
    }
}
