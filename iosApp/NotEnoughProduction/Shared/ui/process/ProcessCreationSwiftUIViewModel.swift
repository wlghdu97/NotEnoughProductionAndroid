//
//  ProcessCreationSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/06.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ProcessCreationSwiftUIViewModel: SwiftUIViewModel<ProcessCreationViewModel>, ObservableObject {
    @Published var processName = "" {
        didSet {
            if oldValue != processName {
                viewModel.changeProcessName(newName: processName)
            }
        }
    }
    @Published var recipePair: ProcessCreationViewModel.RecipeWithElement?
    @Published var isNameValid = true
    @Published var dismiss = false

    @Published var isSnackBarPresented: Bool = false
    @Published var snackBarMessage: String = ""

    private var itemBrowserFactory: ComponentFactory<ProcessItemBrowserSwiftUIViewModel.Component>?

    init(viewModel: ProcessCreationViewModel,
         itemBrowserFactory: ComponentFactory<ProcessItemBrowserSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.itemBrowserFactory = itemBrowserFactory

        viewModel.toCommonFlow(flow: viewModel.processName).watch { name in
            if let name = name as? String {
                self.processName = name
            }
        }

        viewModel.toCommonFlow(flow: viewModel.recipePair).watch { pair in
            if let pair = pair as? ProcessCreationViewModel.RecipeWithElement {
                self.recipePair = pair
            }
        }

        viewModel.toCommonFlow(flow: viewModel.isNameValid).watch { isValid in
            if let isValid = isValid as? Bool {
                self.isNameValid = isValid
            }
        }

        viewModel.toCommonFlow(flow: viewModel.creationErrorMessage).watch { msg in
            if let msg = msg as? String {
                self.snackBarMessage = msg
                self.isSnackBarPresented = true
            } else {
                self.snackBarMessage = ""
            }
        }

        viewModel.toCommonFlow(flow: viewModel.dismiss).watch { dismiss in
            self.dismiss = true
        }
    }

    override init() {
        super.init()
    }

    func createProcess() {
        viewModel.createProcess()
    }

    func createItemBrowserViewModel() -> ProcessItemBrowserSwiftUIViewModel {
        if let viewModel = itemBrowserFactory?.build(()) {
            viewModel.setProcessRecipeSubmittionListener(self)
            return viewModel
        } else {
            let viewModel = ProcessItemBrowserSwiftUIViewModel()
            viewModel.setProcessRecipeSubmittionListener(self)
            return viewModel
        }
    }
}

extension ProcessCreationSwiftUIViewModel: ProcessRecipeSubmissionListener {
    func onSubmit(_ recipe: ModelRecipe, keyElement: ModelRecipeElement) {
        viewModel.submitRecipe(recipe: recipe, keyElement: keyElement)
    }
}

protocol ProcessRecipeSubmissionListener {
    func onSubmit(_ recipe: ModelRecipe, keyElement: ModelRecipeElement)
}

extension ProcessCreationSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ProcessCreationSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ProcessCreationViewModel.self)
                .to(factory: ProcessCreationViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ProcessCreationSwiftUIViewModel>) -> BindingReceipt<ProcessCreationSwiftUIViewModel> {
            bind.to { (viewModel: ProcessCreationViewModel, itemBrowserFactory: ComponentFactory<ProcessItemBrowserSwiftUIViewModel.Component>) in
                ProcessCreationSwiftUIViewModel(viewModel: viewModel, itemBrowserFactory: itemBrowserFactory)
            }
        }
    }
}
