//
//  NotEnoughProductionApp.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/10/06.
//

import SwiftUI
import Shared

@main
struct NotEnoughProductionApp: App {

    init() {
        LoggerInitializer().initialize()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
