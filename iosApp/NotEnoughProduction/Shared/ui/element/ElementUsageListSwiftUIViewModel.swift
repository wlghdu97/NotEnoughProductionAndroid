//
//  ElementUsageSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/23.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ElementUsageListSwiftUIViewModel: SwiftUIViewModel<UsageListViewModel>, ObservableObject {
    @Published var usageList = [ModelRecipeElement]()
    @Published var totalCount = 0
    @Published var isIconLoaded = false

    private weak var usagePager: Multiplatform_pagingFinitePager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.usagePager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newUsageList = (list as? [ModelRecipeElement] ?? [])
                    if !newUsageList.isEmpty {
                        self.usageList = newUsageList
                    }
                })
                self.totalCount = Int(pager.getTotalCount())
            }
        }
    }

    private var elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>?

    init(viewModel: UsageListViewModel,
         elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.elementDetailFactory = elementDetailFactory

        viewModel.toCommonFlow(flow: viewModel.usageList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingFinitePager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.usagePager = pager
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

    func doInit(_ elementId: Int64) {
        viewModel.doInit(elementId: KotlinLong(value: elementId))
    }

    func loadMoreItems() {
        usagePager?.loadNext()
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

extension ElementUsageListSwiftUIViewModel: ElementListener {
    func onClick(elementId: Int64) {
        viewModel.onClick(elementId: elementId)
    }
}

extension ElementUsageListSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ElementUsageListSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(UsageListViewModel.self)
                .to(factory: UsageListViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ElementUsageListSwiftUIViewModel>) -> BindingReceipt<ElementUsageListSwiftUIViewModel> {
            bind.to { (viewModel: UsageListViewModel, elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) in
                ElementUsageListSwiftUIViewModel(viewModel: viewModel, elementDetailFactory: elementDetailFactory)
            }
        }
    }
}
