//
//  ContentView.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/10/06.
//

import SwiftUI
import Shared

struct ContentView: View {
    var body: some View {
        Text(MR.strings().app_name.desc().localized())
            .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
