//
//  MachineBrowser.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import SwiftUI
import Shared

struct MachineBrowser: View {
    @StateObject var viewModel: MachineBrowserSwiftUIViewModel

    var body: some View {
        Group {
            if viewModel.isDBLoaded {
                List { [items = viewModel.machineList] in
                    ForEach(items, id: \.id) { machine in
                        MachineItem(machine: machine)
                            .equatable()
                            .onAppear {
                                if items.last == machine {
                                    viewModel.loadMoreMachines()
                                }
                            }
                    }
                }
                .listStyle(.grouped)
            } else {
                Text(MR.strings().txt_db_not_loaded.desc().localized())
            }
        }
        .animation(.default, value: viewModel.isDBLoaded)
        .navigationTitle(MR.strings().menu_machine_browser.desc().localized())
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct MachineBrowser_Previews: PreviewProvider {
    static var previews: some View {
        MachineBrowser(viewModel: MachineBrowserSwiftUIViewModel())
    }
}
