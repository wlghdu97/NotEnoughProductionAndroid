//
//  MainSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//
//  swiftlint:disable nesting
//  swiftlint:disable closure_parameter_position
//

import SwiftUI
import Cleanse
import Shared

final class MainSwiftUIViewModel: ObservableObject {
    private var itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>?
    private var machineBrowserFactory: ComponentFactory<MachineBrowserSwiftUIViewModel.Component>?

    init(itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>,
         machineBrowserFactory: ComponentFactory<MachineBrowserSwiftUIViewModel.Component>) {
        self.itemBrowserFactory = itemBrowserFactory
        self.machineBrowserFactory = machineBrowserFactory
    }

    init() { }

    func createItemBrowserViewModel() -> ItemBrowserSwiftUIViewModel {
        if let viewModel = itemBrowserFactory?.build(()) {
            return viewModel
        } else {
            return ItemBrowserSwiftUIViewModel()
        }
    }

    func createMachineBrowserViewModel() -> MachineBrowserSwiftUIViewModel {
        if let viewModel = machineBrowserFactory?.build(()) {
            return viewModel
        } else {
            return MachineBrowserSwiftUIViewModel()
        }
    }
}

extension MainSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = MainSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) { }

        static func configureRoot(binder bind: ReceiptBinder<MainSwiftUIViewModel>) -> BindingReceipt<MainSwiftUIViewModel> {
            bind.to { (itemBrowserFactory: ComponentFactory<ItemBrowserSwiftUIViewModel.Component>,
                       machineBrowserFactory: ComponentFactory<MachineBrowserSwiftUIViewModel.Component>) in
                MainSwiftUIViewModel(itemBrowserFactory: itemBrowserFactory, machineBrowserFactory: machineBrowserFactory)
            }
        }
    }
}
