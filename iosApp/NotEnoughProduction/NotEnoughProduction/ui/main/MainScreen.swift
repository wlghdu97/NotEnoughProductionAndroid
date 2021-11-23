//
//  MainScreen.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import SwiftUI
import Introspect
import Shared

struct MainScreen: View {
    @StateObject var viewModel: MainSwiftUIViewModel
    @State private var selection: Int? = 0

    var body: some View {
        let nav = NavigationView {
            let list = List {
                NavigationLink(destination: ItemBrowser(viewModel: viewModel.createItemBrowserViewModel()), tag: 0, selection: $selection) {
                    Label(MR.strings().menu_item_browser.desc().localized(), systemImage: "books.vertical.fill")
                }
                NavigationLink(destination: MachineBrowser(viewModel: viewModel.createMachineBrowserViewModel()), tag: 1, selection: $selection) {
                    Label(MR.strings().menu_machine_browser.desc().localized(), systemImage: "shippingbox.fill")
                }
                NavigationLink(destination: ProcessList(viewModel: viewModel.createProcessListViewModel()), tag: 2, selection: $selection) {
                    Label(MR.strings().menu_process.desc().localized(), systemImage: "flowchart.fill")
                }
                NavigationLink(destination: SettingsScreen(), tag: 3, selection: $selection) {
                    Label(MR.strings().menu_settings.desc().localized(), systemImage: "gearshape.fill")
                }
            }
            .navigationTitle(MR.strings().app_name.desc().localized())
            .navigationBarTitleDisplayMode(.inline)
            if isPad {
                list.listStyle(.sidebar)
            } else {
                list.listStyle(.automatic)
            }
            if isPad {
                EmptyView()
            }
            if isPad {
                EmptyView()
            }
        }
        if isPad {
            nav.navigationViewStyle(.columns)
                .introspectSplitViewController { svc in
                    // this will show sidebar at startup
                    if isPad {
                        svc.show(.primary)
                    }
                }
        } else {
            nav.navigationViewStyle(.stack)
        }
    }
}

extension MainScreen {
    fileprivate var isPad: Bool {
        UIDevice.current.userInterfaceIdiom == .pad
    }
}

struct MainScreen_Previews: PreviewProvider {
    static var previews: some View {
        MainScreen(viewModel: MainSwiftUIViewModel())
    }
}
