//
//  ElementOreDictListSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/24.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class ElementOreDictListSwiftUIViewModel: SwiftUIViewModel<OreDictListViewModel>, ObservableObject {
    @Published var oreDictNameList = [String]()

    private weak var oreDictNamePager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.oreDictNamePager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newNameList = (list as? [String] ?? [])
                    if !newNameList.isEmpty {
                        self.oreDictNameList = newNameList
                    }
                })
            }
        }
    }

    private var replacementListFactory: ComponentFactory<ElementReplacementListSwiftUIViewModel.Component>?

    init(viewModel: OreDictListViewModel,
         replacementListFactory: ComponentFactory<ElementReplacementListSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.replacementListFactory = replacementListFactory

        viewModel.toCommonFlow(flow: viewModel.oreDictNameList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.oreDictNamePager = pager
        }
    }

    override init() {
        super.init()
    }

    func doInit(_ elementId: Int64) {
        viewModel.doInit(elementId: KotlinLong(value: elementId))
    }

    func loadMoreNames() {
        oreDictNamePager?.loadNext()
    }

    func createReplacementListViewModel(_ oreDictName: String) -> ElementReplacementListSwiftUIViewModel {
        if let viewModel = replacementListFactory?.build(()) {
            viewModel.doInit(oreDictName)
            return viewModel
        } else {
            let viewModel = ElementReplacementListSwiftUIViewModel()
            viewModel.doInit(oreDictName)
            return viewModel
        }
    }
}

extension ElementOreDictListSwiftUIViewModel: OreDictListener {
    func onClicked(oreDictName: String) {
        viewModel.onClicked(oreDictName: oreDictName)
    }
}

extension ElementOreDictListSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = ElementOreDictListSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(OreDictListViewModel.self)
                .to(factory: OreDictListViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<ElementOreDictListSwiftUIViewModel>) -> BindingReceipt<ElementOreDictListSwiftUIViewModel> {
            bind.to { (viewModel: OreDictListViewModel, replacementListFactory: ComponentFactory<ElementReplacementListSwiftUIViewModel.Component>) in
                ElementOreDictListSwiftUIViewModel(viewModel: viewModel, replacementListFactory: replacementListFactory)
            }
        }
    }
}
