//
//  MainScreen.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import SwiftUI
import Shared

struct MainScreen: View {
    @StateObject var viewModel: MainSwiftUIViewModel

    var body: some View {
        TabView {
            ItemBrowser(viewModel: viewModel.createItemBrowserViewModel())
                .tabItem {
                    Label(MR.strings().menu_item_browser.desc().localized(), systemImage: "books.vertical.fill")
                }
            MachineBrowser(viewModel: viewModel.createMachineBrowserViewModel())
                .tabItem {
                    Label(MR.strings().menu_machine_browser.desc().localized(), systemImage: "shippingbox.fill")
                }
        }
    }
}

struct MainScreen_Previews: PreviewProvider {
    static var previews: some View {
        MainScreen(viewModel: MainSwiftUIViewModel())
    }
}
