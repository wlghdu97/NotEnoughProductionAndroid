//
//  ProcessImportSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ProcessImportSwiftUIViewModel: SwiftUIViewModel<ProcessImportViewModel>, ObservableObject {
    @Published var isStringValid = false
    @Published var dismiss = false

    override init(viewModel: ProcessImportViewModel) {
        super.init(viewModel: viewModel)

        viewModel.toCommonFlow(flow: viewModel.isStringValid).watch { isValid in
            if let isValid = isValid as? Bool {
                self.isStringValid = isValid
            }
        }

        viewModel.toCommonFlow(flow: viewModel.dismiss).watch { _ in
            self.dismiss = true
        }
    }

    override init() {
        super.init()
    }

    func notifyTextChanged() {
        viewModel.notifyTextChanged()
    }

    func importProcess(_ importString: String) {
        viewModel.importProcess(string: importString)
    }
}

extension ProcessImportSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ProcessImportSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ProcessImportViewModel.self)
                .to(factory: ProcessImportViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ProcessImportSwiftUIViewModel>) -> BindingReceipt<ProcessImportSwiftUIViewModel> {
            bind.to { (viewModel: ProcessImportViewModel) in
                ProcessImportSwiftUIViewModel(viewModel: viewModel)
            }
        }
    }
}
