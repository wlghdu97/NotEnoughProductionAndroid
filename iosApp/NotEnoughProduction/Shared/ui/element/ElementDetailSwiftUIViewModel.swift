//
//  ElementDetailSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//
//  swiftlint:disable nesting
//  swiftlint:disable closure_parameter_position
//

import SwiftUI
import Cleanse
import Shared

final class ElementDetailSwiftUIViewModel: SwiftUIViewModel<ElementDetailViewModel>, ObservableObject {
    @Published var element: ModelRecipeElement?

    private var recipeListFactory: ComponentFactory<ElementRecipeListSwiftUIViewModel.Component>?
    private var usageListFactory: ComponentFactory<ElementUsageListSwiftUIViewModel.Component>?
    private var oreDictNameListFactory: ComponentFactory<ElementOreDictListSwiftUIViewModel.Component>?

    init(viewModel: ElementDetailViewModel,
         recipeListFactory: ComponentFactory<ElementRecipeListSwiftUIViewModel.Component>,
         usageListFactory: ComponentFactory<ElementUsageListSwiftUIViewModel.Component>,
         oreDictNameListFactory: ComponentFactory<ElementOreDictListSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.recipeListFactory = recipeListFactory
        self.usageListFactory = usageListFactory
        self.oreDictNameListFactory = oreDictNameListFactory

        viewModel.toCommonFlow(flow: viewModel.element).watch { element in
            if let element = element as? ModelRecipeElement {
                self.element = element
            }
        }
    }

    override init() {
        super.init()
    }

    func doInit(_ elementId: Int64) {
        viewModel.doInit(elementId: elementId)
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

    func createUsageListViewModel(_ elementId: Int64) -> ElementUsageListSwiftUIViewModel {
        if let viewModel = usageListFactory?.build(()) {
            viewModel.doInit(elementId)
            return viewModel
        } else {
            let viewModel = ElementUsageListSwiftUIViewModel()
            viewModel.doInit(elementId)
            return viewModel
        }
    }

    func createOreDictListViewModel(_ elementId: Int64) -> ElementOreDictListSwiftUIViewModel {
        if let viewModel = oreDictNameListFactory?.build(()) {
            viewModel.doInit(elementId)
            return viewModel
        } else {
            let viewModel = ElementOreDictListSwiftUIViewModel()
            viewModel.doInit(elementId)
            return viewModel
        }
    }
}

extension ElementDetailSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ElementDetailSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ElementDetailViewModel.self)
                .to(factory: ElementDetailViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ElementDetailSwiftUIViewModel>) -> BindingReceipt<ElementDetailSwiftUIViewModel> {
            bind.to { (viewModel: ElementDetailViewModel,
                       recipeListFactory: ComponentFactory<ElementRecipeListSwiftUIViewModel.Component>,
                       usageListFactory: ComponentFactory<ElementUsageListSwiftUIViewModel.Component>,
                       oreDictNameListFactory: ComponentFactory<ElementOreDictListSwiftUIViewModel.Component>) in
                ElementDetailSwiftUIViewModel(viewModel: viewModel,
                                              recipeListFactory: recipeListFactory,
                                              usageListFactory: usageListFactory,
                                              oreDictNameListFactory: oreDictNameListFactory)
            }
        }
    }
}
