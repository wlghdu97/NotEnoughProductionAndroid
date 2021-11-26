//
//  MachineResultsSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/26.
//

import SwiftUI
import Cleanse
import Shared

final class MachineResultSwiftUIViewModel: SwiftUIViewModel<MachineResultViewModel>, ObservableObject {
    @Published var machine: ModelMachine?
    @Published var resultList = [ModelRecipeElement]()
    @Published var isIconLoaded = false

    private weak var resultPager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.resultPager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newResultList = (list as? [ModelRecipeElement] ?? [])
                    if !newResultList.isEmpty {
                        self.resultList = newResultList
                    }
                })
            }
        }
    }

    private var elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>?

    init(viewModel: MachineResultViewModel,
         elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.elementDetailFactory = elementDetailFactory

        viewModel.toCommonFlow(flow: viewModel.machine).watch { machine in
            if let machine = machine as? ModelMachine {
                self.machine = machine
            }
        }

        viewModel.toCommonFlow(flow: viewModel.resultList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.resultPager = pager
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

    func doInit(_ machineId: Int32) {
        viewModel.doInit(machineId: KotlinInt(int: machineId))
    }

    func loadMoreItems() {
        resultPager?.loadNext()
    }

    func searchResults(_ term: String) {
        viewModel.searchResults(term: term)
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

extension MachineResultSwiftUIViewModel: ElementListener {
    func onClick(elementId: Int64) {
        viewModel.onClick(elementId: elementId)
    }
}

extension MachineResultSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = MachineResultSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(MachineResultViewModel.self)
                .to(factory: MachineResultViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<MachineResultSwiftUIViewModel>) -> BindingReceipt<MachineResultSwiftUIViewModel> {
            bind.to { (viewModel: MachineResultViewModel, elementDetailFactory: ComponentFactory<ElementDetailSwiftUIViewModel.Component>) in
                MachineResultSwiftUIViewModel(viewModel: viewModel, elementDetailFactory: elementDetailFactory)
            }
        }
    }
}
