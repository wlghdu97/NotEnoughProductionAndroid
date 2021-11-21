//
//  ProcessList.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import SwiftUI
import Shared

struct ProcessList: View {
    @StateObject var viewModel: ProcessListSwiftUIViewModel
    @State private var showProcessCreationDialog = false
    @State private var showProcessImportDialog = false
    private let stringResolver = StringResolver()

    var body: some View {
        NavigationView {
            List { [items = viewModel.processList] in
                ForEach(items, id: \.processId) { process in
                    ProcessSummaryItem(process: process, processListener: viewModel)
                        .equatable()
                        .swipeActions {
                            Button {
                                viewModel.onDelete(id: process.processId, name: process.name)
                            } label: {
                                Text(MR.strings().btn_delete.desc().localized())
                            }
                            .tint(.red)
                        }
                        .onAppear {
                            if items.last == process {
                                viewModel.loadMoreProcesses()
                            }
                        }
                }
            }
            .animation(.default, value: viewModel.processList)
            .listStyle(.grouped)
            .navigationTitle(MR.strings().app_name.desc().localized())
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Menu {
                        Button {
                            showProcessCreationDialog = true
                        } label: {
                            Text(MR.strings().menu_create_new.desc().localized())
                        }
                        .disabled(true)
                        Button {
                            showProcessImportDialog = true
                        } label: {
                            Text(MR.strings().menu_import_from_string.desc().localized())
                        }
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
        }
        .sheet(item: $viewModel.renameProcess) { idName in
            ProcessRenameSheet(viewModel: viewModel.createProcessRenameViewModel(idName.id, processName: idName.name),
                               showSheet: $viewModel.showRenameProcessDialog)
        }
        .sheet(item: $viewModel.exportString) { exp in
            ProcessExportSheet(exportString: exp.exportString, showSheet: $viewModel.showExportStringDialog)
        }
        .sheet(isPresented: $showProcessImportDialog) {
            ProcessImportSheet(viewModel: viewModel.createProcessImportViewModel(), showSheet: $showProcessImportDialog)
        }
        .alert(stringResolver.formatString(format: MR.strings().title_delete_process, args: viewModel.deleteProcess?.name ?? ""),
               isPresented: $viewModel.showDeleteProcessDialog,
               presenting: viewModel.deleteProcess) { idName in
            Button(MR.strings().btn_delete.desc().localized(), role: .destructive) {
                viewModel.deleteProcess(idName.id)
            }
            Button(MR.strings().btn_cancel.desc().localized(), role: .cancel) {
                viewModel.showDeleteProcessDialog = false
            }
        } message: { _ in
            Text(MR.strings().txt_action_cannot_be_undone.desc().localized())
        }
    }
}

struct ProcessList_Previews: PreviewProvider {
    static var previews: some View {
        ProcessList(viewModel: ProcessListSwiftUIViewModel())
    }
}
