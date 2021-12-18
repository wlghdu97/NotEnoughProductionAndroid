//
//  NotEnoughProductionApp.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/10/06.
//
//  swiftlint:disable force_try
//

import SwiftUI
import Cleanse
import Shared

@main
class NotEnoughProductionApp: App {
    private var factory: ComponentFactory<AppComponent>!
    private var mainFactory: ComponentFactory<MainSwiftUIViewModel.Component>!
    private var settingsFactory: ComponentFactory<SettingsSwiftUIViewModel.Component>!

    required init() {
        LoggerInitializer().initialize()

        // injections
        factory = try! ComponentFactory.of(AppComponent.self)
        let injector = factory.build(())
        injector.injectProperties(into: self)
        precondition(mainFactory != nil)
    }

    var body: some Scene {
        WindowGroup {
            MainScreen(viewModel: self.mainFactory.build(()), settingsVieWModel: self.settingsFactory.build(()))
        }
    }
}

extension NotEnoughProductionApp {
    func injectProperties(_ mainFactory: ComponentFactory<MainSwiftUIViewModel.Component>, settingsFactory: ComponentFactory<SettingsSwiftUIViewModel.Component>) {
        self.mainFactory = mainFactory
        self.settingsFactory = settingsFactory
    }
}
