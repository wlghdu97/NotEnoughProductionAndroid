//
//  ItemBrowserSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ItemBrowserSwiftUIViewModel: SwiftUIViewModel<ItemBrowserViewModel>, ObservableObject {
    @Published var itemList = [ModelRecipeElement]()
    @Published var matchedCount = 0
    @Published var isDBLoaded = false
    @Published var isIconLoaded = false

    private var elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>?

    private weak var itemPager: Multiplatform_pagingFinitePager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.itemPager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newItemList = (list as? [ModelRecipeElement] ?? [])
                    if !newItemList.isEmpty {
                        self.itemList = newItemList
                    }
                    self.matchedCount = Int(pager.getTotalCount())
                })
            }
        }
    }

    init(viewModel: ItemBrowserViewModel,
         elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.elementDetailFactory = elementDetailFactory

        viewModel.toCommonFlow(flow: viewModel.elementSearchResult).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingFinitePager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.itemPager = pager
        }

        viewModel.toCommonFlow(flow: viewModel.isDBLoaded).watch { isLoaded in
            if let isLoaded = isLoaded as? Bool {
                self.isDBLoaded = isLoaded
            }
        }

        viewModel.toCommonFlow(flow: viewModel.isIconLoaded).watch { isLoaded in
            if let isLoaded = isLoaded as? Bool {
                self.isIconLoaded = isLoaded
            }
        }

        // load initial list
        search("")
    }

    override init() {
        super.init()
    }

    func search(_ searchTerm: String) {
        viewModel.searchElements(term: searchTerm)
    }

    func loadMoreItems() {
        itemPager?.loadNext()
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

extension ItemBrowserSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ItemBrowserSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ItemBrowserViewModel.self)
                .to(factory: ItemBrowserViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ItemBrowserSwiftUIViewModel>) -> BindingReceipt<ItemBrowserSwiftUIViewModel> {
            bind.to { (viewModel: ItemBrowserViewModel, elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) in
                ItemBrowserSwiftUIViewModel(viewModel: viewModel, elementDetailFactory: elementDetailFactory)
            }
        }
    }
}
