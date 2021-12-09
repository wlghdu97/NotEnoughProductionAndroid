//
//  ProcessListSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//
//  swiftlint:disable nesting
//  swiftlint:disable identifier_name
//  swiftlint:disable closure_parameter_position
//

import SwiftUI
import Cleanse
import Shared

final class ProcessListSwiftUIViewModel: SwiftUIViewModel<ProcessListViewModel>, ObservableObject {
    @Published var processList = [ModelProcessSummary]()
    @Published var isIconLoaded = false

    @Published var showRenameProcessDialog = false {
        didSet {
            if !showRenameProcessDialog {
                renameProcess = nil
            }
        }
    }
    @Published var renameProcess: ProcessListViewModel.ProcessIdName?
    @Published var showExportStringDialog = false {
        didSet {
            if !showExportStringDialog {
                exportString = nil
            }
        }
    }
    @Published var exportString: ExportString?
    @Published var showExportFailedMessage = false
    @Published var showDeleteProcessDialog = false {
        didSet {
            if !showDeleteProcessDialog {
                deleteProcess = nil
            }
        }
    }
    @Published var deleteProcess: ProcessListViewModel.ProcessIdName?

    private var processRenameFactory: ComponentFactory<ProcessRenameSwiftUIViewModel.Component>?
    private var processImportFactory: ComponentFactory<ProcessImportSwiftUIViewModel.Component>?
    private var processCreationFactory: ComponentFactory<ProcessCreationSwiftUIViewModel.Component>?

    private weak var processPager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.processPager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newProcessList = (list as? [ModelProcessSummary] ?? [])
                    if !newProcessList.isEmpty {
                        self.processList = newProcessList
                    }
                })
            }
        }
    }

    init(viewModel: ProcessListViewModel,
         processRenameFactory: ComponentFactory<ProcessRenameSwiftUIViewModel.Component>,
         processImportFactory: ComponentFactory<ProcessImportSwiftUIViewModel.Component>,
         processCreationFactory: ComponentFactory<ProcessCreationSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.processRenameFactory = processRenameFactory
        self.processImportFactory = processImportFactory
        self.processCreationFactory = processCreationFactory

        viewModel.toCommonFlow(flow: viewModel.processList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.processPager = pager
        }

        viewModel.toCommonFlow(flow: viewModel.isIconLoaded).watch { isLoaded in
            if let isLoaded = isLoaded as? Bool {
                self.isIconLoaded = isLoaded
            }
        }

        viewModel.toCommonFlow(flow: viewModel.renameProcess).watch { pair in
            if let pair = pair as? ProcessListViewModel.ProcessIdName {
                self.renameProcess = pair
                self.showRenameProcessDialog = true
            }
        }

        viewModel.toCommonFlow(flow: viewModel.showExportStringDialog).watch { exportString in
            if let exportString = exportString as? String {
                self.exportString = ExportString(exportString: exportString)
                self.showExportStringDialog = true
            }
        }

        viewModel.toCommonFlow(flow: viewModel.deleteProcess).watch { pair in
            if let pair = pair as? ProcessListViewModel.ProcessIdName {
                self.deleteProcess = pair
                self.showDeleteProcessDialog = true
            }
        }
    }

    override init() {
        super.init()
    }

    func loadMoreProcesses() {
        processPager?.loadNext()
    }

    func deleteProcess(_ processId: String) {
        viewModel.deleteProcess(processId: processId)
    }

    func createProcessRenameViewModel(_ processId: String, processName: String) -> ProcessRenameSwiftUIViewModel {
        if let viewModel = processRenameFactory?.build(()) {
            viewModel.doInit(processId, processName: processName)
            return viewModel
        } else {
            let viewModel = ProcessRenameSwiftUIViewModel()
            viewModel.doInit(processId, processName: processName)
            return viewModel
        }
    }

    func createProcessImportViewModel() -> ProcessImportSwiftUIViewModel {
        if let viewModel = processImportFactory?.build(()) {
            return viewModel
        } else {
            return ProcessImportSwiftUIViewModel()
        }
    }

    func createProcessCreationViewModel() -> ProcessCreationSwiftUIViewModel {
        if let viewModel = processCreationFactory?.build(()) {
            return viewModel
        } else {
            return ProcessCreationSwiftUIViewModel()
        }
    }
}

extension ProcessListSwiftUIViewModel: ProcessListener {
    func onClick(id: String, name: String) {
        viewModel.onClick(id: id, name: name)
    }

    func onDelete(id: String, name: String) {
        viewModel.onDelete(id: id, name: name)
    }

    func onExportString(id: String) {
        viewModel.onExportString(id: id)
    }

    func onRename(id: String, prevName: String) {
        viewModel.onRename(id: id, prevName: prevName)
    }
}

extension ProcessListSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ProcessListSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ProcessListViewModel.self)
                .to(factory: ProcessListViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ProcessListSwiftUIViewModel>) -> BindingReceipt<ProcessListSwiftUIViewModel> {
            bind.to { (viewModel: ProcessListViewModel,
                       processRenameFactory: ComponentFactory<ProcessRenameSwiftUIViewModel.Component>,
                       processImportFactory: ComponentFactory<ProcessImportSwiftUIViewModel.Component>,
                       processCreationFactory: ComponentFactory<ProcessCreationSwiftUIViewModel.Component>) in
                ProcessListSwiftUIViewModel(viewModel: viewModel,
                                            processRenameFactory: processRenameFactory,
                                            processImportFactory: processImportFactory,
                                            processCreationFactory: processCreationFactory)
            }
        }
    }
}

struct ExportString: Identifiable {
    var id: String { exportString }
    let exportString: String
}
