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
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    @StateObject var viewModel: MainSwiftUIViewModel
    @StateObject var settingsVieWModel: SettingsSwiftUIViewModel
    @State private var selection: Int? = 0
    @State private var firstLoad = true

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
                NavigationLink(destination: SettingsScreen(viewModel: settingsVieWModel), tag: 3, selection: $selection) {
                    Label(MR.strings().menu_settings.desc().localized(), systemImage: "gearshape.fill")
                }
            }
            .navigationTitle(MR.strings().app_name.desc().localized())
            .navigationBarTitleDisplayMode(.inline)
            if widthRegular {
                list.listStyle(.sidebar)
            } else {
                list.listStyle(.automatic)
            }
            if widthRegular {
                EmptyView()
            }
            if widthRegular {
                EmptyView()
            }
        }
        if widthRegular {
            nav.navigationViewStyle(.columns)
                .introspectSplitViewController { svc in
                    // this will show sidebar at startup
                    if widthRegular && firstLoad {
                        svc.show(.primary)
                        firstLoad = false
                    }
                }
        } else {
            nav.navigationViewStyle(.stack)
        }
    }
}

extension MainScreen {
    fileprivate var widthRegular: Bool {
        horizontalSizeClass == .regular
    }
}

struct MainScreen_Previews: PreviewProvider {
    static var previews: some View {
        MainScreen(viewModel: MainSwiftUIViewModel(), settingsVieWModel: SettingsSwiftUIViewModel())
    }
}
