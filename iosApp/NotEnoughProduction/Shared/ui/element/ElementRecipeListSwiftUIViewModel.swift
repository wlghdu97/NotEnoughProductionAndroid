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

    private weak var recipePager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.recipePager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newRecipeList = (list as? [ModelRecipeMachineView] ?? [])
                    if !newRecipeList.isEmpty {
                        self.recipeList = newRecipeList
                    }
                })
            }
        }
    }

    override init(viewModel: RecipeListViewModel_) {
        super.init(viewModel: viewModel)

        viewModel.toCommonFlow(flow: viewModel.recipeList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
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
    }

    func loadMoreItems() {
        recipePager?.loadNext()
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
            bind.to { (viewModel: RecipeListViewModel_) in
                ElementRecipeListSwiftUIViewModel(viewModel: viewModel)
            }
        }
    }
}
