//
//  MachineBrowserSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import Shared

final class MachineBrowserSwiftUIViewModel: SwiftUIViewModel<MachineBrowserViewModel>, ObservableObject {
    @Published var machineList = [ModelMachine]()
    @Published var isDBLoaded = false

    private weak var machinePager: Multiplatform_pagingPager<AnyObject, AnyObject>? {
        didSet {
            if let pager = self.machinePager {
                viewModel.toPagingData(pager: pager).watch(block: { [unowned self] list in
                    let newMachineList = (list as? [ModelMachine] ?? [])
                    if !newMachineList.isEmpty {
                        self.machineList = newMachineList
                    }
                })
            }
        }
    }

    private var machineResultFactory: ComponentFactory<MachineResultSwiftUIViewModel.Component>?

    init(viewModel: MachineBrowserViewModel,
         machineResultFactory: ComponentFactory<MachineResultSwiftUIViewModel.Component>) {
        super.init(viewModel: viewModel)
        self.machineResultFactory = machineResultFactory

        viewModel.toCommonFlow(flow: viewModel.machineList).watch { [unowned self] pager in
            guard let pager = pager as? Multiplatform_pagingPager<AnyObject, AnyObject> else {
                debugPrint("item pager not found.")
                return
            }
            self.machinePager = pager
        }

        viewModel.toCommonFlow(flow: viewModel.isDBLoaded).watch { isLoaded in
            if let isLoaded = isLoaded as? Bool {
                self.isDBLoaded = isLoaded
            }
        }
    }

    override init() {
        super.init()
    }

    func loadMoreMachines() {
        machinePager?.loadNext()
    }

    func createMachineResultViewModel(_ machineId: Int32) -> MachineResultSwiftUIViewModel {
        if let viewModel = machineResultFactory?.build(()) {
            viewModel.doInit(machineId)
            return viewModel
        } else {
            let viewModel = MachineResultSwiftUIViewModel()
            viewModel.doInit(machineId)
            return viewModel
        }
    }
}

extension MachineBrowserSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = MachineBrowserSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(MachineBrowserViewModel.self)
                .to(factory: MachineBrowserViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<MachineBrowserSwiftUIViewModel>) -> BindingReceipt<MachineBrowserSwiftUIViewModel> {
            bind.to { (viewModel: MachineBrowserViewModel, machineResultFactory: ComponentFactory<MachineResultSwiftUIViewModel.Component>) in
                MachineBrowserSwiftUIViewModel(viewModel: viewModel, machineResultFactory: machineResultFactory)
            }
        }
    }
}
