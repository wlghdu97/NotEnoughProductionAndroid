//
//  ElementRecipeListSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/23.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ElementRecipeListSwiftUIViewModel: SwiftUIViewModel<RecipeListViewModel_>, ObservableObject {
    @Published var recipeList = [ModelRecipeMachineView]()
    @Published var totalCount = 0

    private var elementId: Int64!

    private weak var recipePager: Multiplatform_pagingFinitePager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.recipePager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newRecipeList = (list as? [ModelRecipeMachineView] ?? [])
                    if !newRecipeList.isEmpty {
                        self.recipeList = newRecipeList
                    }
                    self.totalCount = Int(pager.getTotalCount())
                })
            }
        }
    }

    private var recipeListFactory: ComponentFactory<MachineRecipeListSwiftUIViewModel.Component>?

    init(viewModel: RecipeListViewModel_,
         recipeListFactory: ComponentFactory<MachineRecipeListSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.recipeListFactory = recipeListFactory

        viewModel.toCommonFlow(flow: viewModel.recipeList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingFinitePager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.recipePager = pager
        }
    }

    override init() {
        super.init()
    }

    func doInit(_ elementId: Int64) {
        viewModel.doInit(elementId: KotlinLong(value: elementId))
        self.elementId = elementId
    }

    func loadMoreItems() {
        recipePager?.loadNext()
    }

    func createMachineRecipeListViewModel(_ machineId: Int32) -> MachineRecipeListSwiftUIViewModel {
        if let viewModel = recipeListFactory?.build(()) {
            viewModel.doInit(elementId: elementId, machineId: machineId)
            return viewModel
        } else {
            let viewModel = MachineRecipeListSwiftUIViewModel()
            viewModel.doInit(elementId: elementId, machineId: machineId)
            return viewModel
        }
    }
}

extension ElementRecipeListSwiftUIViewModel: MachineListener {
    func onClick(machineId: Int32) {
        viewModel.onClick(machineId: machineId)
    }
}

extension ElementRecipeListSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ElementRecipeListSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(RecipeListViewModel_.self)
                .to(factory: RecipeListViewModel_.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ElementRecipeListSwiftUIViewModel>) -> BindingReceipt<ElementRecipeListSwiftUIViewModel> {
            bind.to { (viewModel: RecipeListViewModel_, recipeListFactory: ComponentFactory<MachineRecipeListSwiftUIViewModel.Component>) in
                ElementRecipeListSwiftUIViewModel(viewModel: viewModel, recipeListFactory: recipeListFactory)
            }
        }
    }
}
