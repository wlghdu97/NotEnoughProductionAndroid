//
//  SettingsScreen.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//

import SwiftUI
import Shared

struct SettingsScreen: View {
    var body: some View {
        Group {
            Text("Nothing here yet")
        }
        .navigationTitle(MR.strings().menu_settings.desc().localized())
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct SettingsScreen_Previews: PreviewProvider {
    static var previews: some View {
        SettingsScreen()
    }
}
