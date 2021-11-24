//
//  UIModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Cleanse
import Shared

struct UIModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.install(dependency: MainSwiftUIViewModel.Component.self)
        binder.install(dependency: ItemBrowserSwiftUIViewModel.Component.self)
        binder.install(dependency: ElementDetailSwiftUIViewModel.Component.self)
        binder.install(dependency: ElementRecipeListSwiftUIViewModel.Component.self)
        binder.install(dependency: ElementUsageListSwiftUIViewModel.Component.self)
        binder.install(dependency: ElementOreDictListSwiftUIViewModel.Component.self)
        binder.install(dependency: ElementReplacementListSwiftUIViewModel.Component.self)
        binder.install(dependency: MachineBrowserSwiftUIViewModel.Component.self)
        binder.install(dependency: ProcessListSwiftUIViewModel.Component.self)
        binder.install(dependency: ProcessRenameSwiftUIViewModel.Component.self)
        binder.install(dependency: ProcessImportSwiftUIViewModel.Component.self)
    }
}
