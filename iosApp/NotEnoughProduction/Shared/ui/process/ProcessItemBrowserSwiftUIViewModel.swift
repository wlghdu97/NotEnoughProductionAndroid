//
//  ProcessItemBrowserSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/07.
//
//  swiftlint:disable nesting
//  swiftlint:disable closure_parameter_position
//

import SwiftUI
import Cleanse
import Shared

final class ProcessItemBrowserSwiftUIViewModel: SwiftUIViewModel<ProcessItemBrowserViewModel>, ObservableObject {
    @Published var dismiss = false

    private var recipeSubmissionListener: ProcessRecipeSubmissionListener?

    private var itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>?
    private var recipeListFactory: ComponentFactory<ElementRecipeListSwiftUIViewModel.Component>?
    private var machineRecipeListFactory: ComponentFactory<MachineRecipeListSwiftUIViewModel.Component>?

    init(viewModel: ProcessItemBrowserViewModel,
         itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>,
         recipeListFactory: ComponentFactory<ElementRecipeListSwiftUIViewModel.Component>,
         machineRecipeListFactory: ComponentFactory<MachineRecipeListSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.itemBrowserFactory = itemBrowserFactory
        self.recipeListFactory = recipeListFactory
        self.machineRecipeListFactory = machineRecipeListFactory

        viewModel.toCommonFlow(flow: viewModel.returnResult).watch { pair in
            if let pair = pair as? ProcessItemBrowserViewModel.RecipeWithElement {
                self.recipeSubmissionListener?.onSubmit(pair.recipe, keyElement: pair.element)
                self.dismiss = true
            }
        }
    }

    override init() {
        super.init()
    }

    func setProcessRecipeSubmittionListener(_ listener: ProcessRecipeSubmissionListener) {
        self.recipeSubmissionListener = listener
    }

    func submit(_ recipe: ModelRecipe, keyElement: ModelRecipeElement) {
        viewModel.onSelect(targetRecipe: recipe, keyElement: keyElement)
    }

    func createItemBrowserViewModel() -> ItemBrowserSwiftUIViewModel {
        if let viewModel = itemBrowserFactory?.build(()) {
            return viewModel
        } else {
            return ItemBrowserSwiftUIViewModel()
        }
    }

    func createRecipeListViewModel(_ elementId: Int64) -> ElementRecipeListSwiftUIViewModel {
        if let viewModel = recipeListFactory?.build(()) {
            viewModel.doInit(elementId)
            return viewModel
        } else {
            let viewModel = ElementRecipeListSwiftUIViewModel()
            viewModel.doInit(elementId)
            return viewModel
        }
    }

    func createMachineRecipeListViewModel(_ elementId: Int64, machineId: Int32) -> MachineRecipeListSwiftUIViewModel {
        if let viewModel = machineRecipeListFactory?.build(()) {
            viewModel.doInit(elementId: elementId, machineId: machineId)
            return viewModel
        } else {
            let viewModel = MachineRecipeListSwiftUIViewModel()
            viewModel.doInit(elementId: elementId, machineId: machineId)
            return viewModel
        }
    }
}

extension ProcessItemBrowserSwiftUIViewModel: ElementListener {
    func onClick(elementId: Int64) {
        viewModel.onClick(elementId: elementId)
    }
}

extension ProcessItemBrowserSwiftUIViewModel: MachineListener {
    func onClick(machineId: Int32) {
        viewModel.onClick(machineId: machineId)
    }
}

extension ProcessItemBrowserSwiftUIViewModel: RootRecipeSelectionListener {
    func onSelect(targetRecipe: ModelRecipe, keyElement: ModelRecipeElement) {
        viewModel.onSelect(targetRecipe: targetRecipe, keyElement: keyElement)
    }
}

extension ProcessItemBrowserSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ProcessItemBrowserSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ProcessItemBrowserViewModel.self)
                .to(factory: ProcessItemBrowserViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ProcessItemBrowserSwiftUIViewModel>) -> BindingReceipt<ProcessItemBrowserSwiftUIViewModel> {
            bind.to { (viewModel: ProcessItemBrowserViewModel,
                       itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>,
                       recipeListFactory: ComponentFactory<ElementRecipeListSwiftUIViewModel.Component>,
                       machineRecipeListFactory: ComponentFactory<MachineRecipeListSwiftUIViewModel.Component>) in
                ProcessItemBrowserSwiftUIViewModel(viewModel: viewModel,
                                                   itemBrowserFactory: itemBrowserFactory,
                                                   recipeListFactory: recipeListFactory,
                                                   machineRecipeListFactory: machineRecipeListFactory)
            }
        }
    }
}
