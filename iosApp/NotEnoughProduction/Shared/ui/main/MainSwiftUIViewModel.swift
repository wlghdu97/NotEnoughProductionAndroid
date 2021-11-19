//
//  MainSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class MainSwiftUIViewModel: ObservableObject {
    private var itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>?

    init(itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>) {
        self.itemBrowserFactory = itemBrowserFactory
    }

    init() { }

    func createItemBrowserViewModel() -> ItemBrowserSwiftUIViewModel {
        if let viewModel = itemBrowserFactory?.build(()) {
            return viewModel
        } else {
            return ItemBrowserSwiftUIViewModel()
        }
    }
}

extension MainSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = MainSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) { }

        static func configureRoot(binder bind: ReceiptBinder<MainSwiftUIViewModel>) -> BindingReceipt<MainSwiftUIViewModel> {
            bind.to { (itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>) in
                MainSwiftUIViewModel(itemBrowserFactory: itemBrowserFactory)
            }
        }
    }
}
