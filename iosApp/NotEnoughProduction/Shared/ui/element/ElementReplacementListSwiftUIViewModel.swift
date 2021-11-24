//
//  ElementReplacementListSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/24.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ElementReplacementListSwiftUIViewModel: SwiftUIViewModel<ReplacementListViewModel>, ObservableObject {
    @Published var replacementList = [ModelRecipeElement]()
    @Published var isIconLoaded = false

    private weak var replacementPager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.replacementPager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newReplacementList = (list as? [ModelRecipeElement] ?? [])
                    if !newReplacementList.isEmpty {
                        self.replacementList = newReplacementList
                    }
                })
            }
        }
    }

    private var elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>?

    init(viewModel: ReplacementListViewModel,
         elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.elementDetailFactory = elementDetailFactory

        viewModel.toCommonFlow(flow: viewModel.replacementList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.replacementPager = pager
        }

        viewModel.toCommonFlow(flow: viewModel.isIconLoaded).watch { isLoaded in
            if let isLoaded = isLoaded as? Bool {
                self.isIconLoaded = isLoaded
            }
        }
    }

    override init() {
        super.init()
    }

    func doInit(_ oreDictName: String) {
        viewModel.doInit(oreDictName: oreDictName)
    }

    func loadMoreItems() {
        replacementPager?.loadNext()
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

extension ElementReplacementListSwiftUIViewModel: ElementListener {
    func onClick(elementId: Int64, elementType: Int32) {
        viewModel.onClick(elementId: elementId, elementType: elementType)
    }
}

extension ElementReplacementListSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ElementReplacementListSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ReplacementListViewModel.self)
                .to(factory: ReplacementListViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ElementReplacementListSwiftUIViewModel>) -> BindingReceipt<ElementReplacementListSwiftUIViewModel> {
            bind.to { (viewModel: ReplacementListViewModel, elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) in
                ElementReplacementListSwiftUIViewModel(viewModel: viewModel, elementDetailFactory: elementDetailFactory)
            }
        }
    }
}
