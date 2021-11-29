//
//  SettingsScreen.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//

import SwiftUI
import ZIPFoundation
import Shared

struct SettingsScreen: View {
    @ObservedObject var viewModel: SettingsSwiftUIViewModel
    @State private var archive: Archive?

    var body: some View {
        Form {
            if let unzipLog = viewModel.iconUnzipLog {
                Section {
                    VStack(alignment: .leading) {
                        Text(MR.strings().title_unzipping.desc().localized())
                        Text(unzipLog.message)
                            .captionText()
                            .lineLimit(1)
                        ProgressView(value: Float(unzipLog.progress), total: 100)
                            .progressViewStyle(.linear)
                    }
                }
            }
            Section {
                Toggle(MR.strings().title_icon_status.desc().localized(), isOn: $viewModel.isIconLoaded)
                    .disabled(viewModel.isUnzipping)
            }
        }
        .navigationTitle(MR.strings().menu_settings.desc().localized())
        .navigationBarTitleDisplayMode(.inline)
        .animation(.default, value: viewModel.isIconLoaded)
        .animation(.default, value: viewModel.iconUnzipLog)
        .sheet(isPresented: $viewModel.showIconZipPicker) {
            ArchivePicker(archive: $archive) {
                viewModel.cancelLoadIcons()
            }
            .interactiveDismissDisabled()
        }
        .onChange(of: archive) { archive in
            if let archive = archive {
                viewModel.reloadIcons(archive)
            }
        }
    }
}

struct SettingsScreen_Previews: PreviewProvider {
    static var previews: some View {
        SettingsScreen(viewModel: SettingsSwiftUIViewModel())
    }
}

extension Archive: Equatable {
    public static func == (lhs: Archive, rhs: Archive) -> Bool {
        lhs.url == rhs.url
    }
}
