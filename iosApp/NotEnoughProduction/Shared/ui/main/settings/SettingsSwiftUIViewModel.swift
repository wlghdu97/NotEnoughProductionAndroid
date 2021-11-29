//
//  SettingsSwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/29.
//
//  swiftlint:disable nesting
//

import SwiftUI
import Cleanse
import ZIPFoundation
import Shared

final class SettingsSwiftUIViewModel: SwiftUIViewModel<SettingsViewModel>, ObservableObject {
    @Published var isFirstDBLoad = false
    @Published var isDBLoaded = false
    @Published var isIconLoaded = false {
        didSet {
            if cancelToggleIcon {
                cancelToggleIcon = false
                return
            }
            if oldValue != isIconLoaded {
                viewModel.toggleIconLoaded()
            }
        }
    }

    @Published var iconUnzipLog: IconUnzipUseCase.Progress?
    @Published var isUnzipping = false
    @Published var showIconZipPicker = false

    private var cancelToggleIcon = true

    override init(viewModel: SettingsViewModel) {
        super.init(viewModel: viewModel)

        viewModel.toCommonFlow(flow: viewModel.isFirstDBLoad).watch { isFirstDBLoad in
            if let isFirstDBLoad = isFirstDBLoad as? Bool {
                self.isFirstDBLoad = isFirstDBLoad
            }
        }

        viewModel.toCommonFlow(flow: viewModel.isDBLoaded).watch { isDBLoaded in
            if let isDBLoaded = isDBLoaded as? Bool {
                self.isDBLoaded = isDBLoaded
            }
        }

        viewModel.toCommonFlow(flow: viewModel.isIconLoaded).watch { isIconLoaded in
            if let isIconLoaded = isIconLoaded as? Bool {
                self.isIconLoaded = isIconLoaded
            }
        }

        viewModel.toCommonFlow(flow: viewModel.iconUnzipLog).watch { progress in
            if let progress = progress as? IconUnzipUseCase.Progress {
                self.iconUnzipLog = progress
            }
        }

        viewModel.toCommonFlow(flow: viewModel.isUnzipping).watch { unzipping in
            if let unzipping = unzipping as? Bool {
                self.isUnzipping = unzipping
            }
        }

        viewModel.toCommonFlow(flow: viewModel.showIconZipPicker).watch { _ in
            self.showIconZipPicker = true
        }
    }

    override init() {
        super.init()
    }

    func toggleIconLoaded() {
        viewModel.toggleIconLoaded()
    }

    func reloadIcons(_ archive: Archive) {
        viewModel.reloadIcons(archiver: SwiftZipArchiver(archive))
    }

    func cancelLoadIcons() {
        cancelToggleIcon = true
        isIconLoaded = false
    }
}

extension SettingsSwiftUIViewModel {
    struct Component: Cleanse.Component {
        typealias Root = SettingsSwiftUIViewModel

        static func configure(binder: Binder<Unscoped>) {
            binder.bind(SettingsViewModel.self)
                .to(factory: SettingsViewModel.init)
        }

        static func configureRoot(binder bind: ReceiptBinder<SettingsSwiftUIViewModel>) -> BindingReceipt<SettingsSwiftUIViewModel> {
            bind.to { (viewModel: SettingsViewModel) in
                SettingsSwiftUIViewModel(viewModel: viewModel)
            }
        }
    }
}
