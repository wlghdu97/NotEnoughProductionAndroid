//
//  MachineRecipeListSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class MachineRecipeListSwiftUIViewModel: SwiftUIViewModel<MachineRecipeListViewModel>, ObservableObject {
    @Published var recipeList = [ModelRecipeView]()
    @Published var isIconLoaded = false

    @Published var navigateToElementDetail = false
    @Published var navigationElementId: Int64!

    private(set) var targetElementId: Int64!

    private weak var recipePager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.recipePager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newRecipeList = (list as? [ModelRecipeView] ?? [])
                    if !newRecipeList.isEmpty {
                        self.recipeList = newRecipeList
                    }
                })
            }
        }
    }

    private var elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>?

    init(viewModel: MachineRecipeListViewModel,
         elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.elementDetailFactory = elementDetailFactory

        viewModel.toCommonFlow(flow: viewModel.recipeList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.recipePager = pager
        }

        viewModel.toCommonFlow(flow: viewModel.isIconLoaded).watch { isLoaded in
            if let isLoaded = isLoaded as? Bool {
                self.isIconLoaded = isLoaded
            }
        }

        viewModel.toCommonFlow(flow: viewModel.navigateToDetail).watch { elementId in
            if let elementId = elementId as? Int64 {
                self.navigationElementId = elementId
                self.navigateToElementDetail = true
            }
        }
    }

    override init() {
        super.init()
    }

    func doInit(elementId: Int64, machineId: Int32) {
        viewModel.doInit(elementId: elementId, machineId: machineId)
        self.targetElementId = elementId
    }

    func loadMoreRecipes() {
        recipePager?.loadNext()
    }

    func searchIngredients(_ term: String) {
        viewModel.searchIngredients(term: term)
    }

    func createElementDetailViewModel(_ elementId: Int64) -> ElementDetailSwiftUIViewModel {
        if let viewModel = elementDetailFactory?.build(()) {
            viewModel.doInit(elementId)
            return viewModel
        } else {
            let viewModel = ElementDetailSwiftUIViewModel()
            viewModel.doInit(elementId)
            return viewModel
        }
    }
}

extension MachineRecipeListSwiftUIViewModel: ElementListener {
    func onClick(elementId: Int64) {
        viewModel.onClick(elementId: elementId)
    }
}

extension MachineRecipeListSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = MachineRecipeListSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(MachineRecipeListViewModel.self)
                .to(factory: MachineRecipeListViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<MachineRecipeListSwiftUIViewModel>) -> BindingReceipt<MachineRecipeListSwiftUIViewModel> {
            bind.to { (viewModel: MachineRecipeListViewModel, elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) in
                MachineRecipeListSwiftUIViewModel(viewModel: viewModel, elementDetailFactory: elementDetailFactory)
            }
        }
    }
}
