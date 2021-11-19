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
    @Published var isDBLoaded = false
    @Published var isIconLoaded = false

    private weak var itemPager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.itemPager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newItemList = (list as? [ModelRecipeElement] ?? [])
                    if !newItemList.isEmpty {
                        self.itemList = newItemList
                    }
                })
            }
        }
    }

    override init(viewModel: ItemBrowserViewModel) {
        super.init(viewModel: viewModel)

        viewModel.toCommonFlow(flow: viewModel.elementSearchResult).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
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
}

extension ItemBrowserSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ItemBrowserSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(ItemBrowserViewModel.self)
                .to(factory: ItemBrowserViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ItemBrowserSwiftUIViewModel>) -> BindingReceipt<ItemBrowserSwiftUIViewModel> {
            bind.to { (viewModel: ItemBrowserViewModel) in
                ItemBrowserSwiftUIViewModel(viewModel: viewModel)
            }
        }
    }
}
