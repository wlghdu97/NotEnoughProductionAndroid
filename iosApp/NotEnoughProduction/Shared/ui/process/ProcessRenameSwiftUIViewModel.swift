//
//  ProcessRenameSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ProcessRenameSwiftUIViewModel: SwiftUIViewModel<ProcessRenameViewModel>, ObservableObject {
    @Published var name = "" {
        didSet {
            viewModel.changeName(name: name)
        }
    }
    @Published var isNameValid = false
    @Published var dismiss = false

    @Published var isSnackBarPresented: Bool = false
    @Published var snackBarMessage: String = ""

    override init(viewModel: ProcessRenameViewModel) {
        super.init(viewModel: viewModel)

        viewModel.toCommonFlow(flow: viewModel.name).watch { name in
            if let name = name as? String, self.name != name {
                self.name = name
            }
        }

        viewModel.toCommonFlow(flow: viewModel.isNameValid).watch { isValid in
            if let isValid = isValid as? Bool {
                self.isNameValid = isValid
            }
        }

        viewModel.toCommonFlow(flow: viewModel.renameErrorMessage).watch { msg in
            if let msg = msg as? String {
                self.snackBarMessage = msg
                self.isSnackBarPresented = true
            } else {
                self.snackBarMessage = ""
            }
        }

        viewModel.toCommonFlow(flow: viewModel.dismiss).watch { dismiss in
            if dismiss != nil {
                self.dismiss = true
            }
        }
    }

    override init() {
        super.init()
    }

    func doInit(_ processId: String, processName: String) {
        viewModel.doInit(processId: processId, name: processName)
    }

    func renameProcess() {
        viewModel.renameProcess()
    }
}

extension ProcessRenameSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ProcessRenameSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ProcessRenameViewModel.self)
                .to(factory: ProcessRenameViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ProcessRenameSwiftUIViewModel>) -> BindingReceipt<ProcessRenameSwiftUIViewModel> {
            bind.to { (viewModel: ProcessRenameViewModel) in
                ProcessRenameSwiftUIViewModel(viewModel: viewModel)
            }
        }
    }
}
