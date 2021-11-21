//
//  ProcessSummaryItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import SwiftUI
import Shared

struct ProcessSummaryItem: View {
    let process: ModelProcessSummary
    let processListener: ProcessListener?

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(process.name)
                Text(process.processDetailsText)
                    .font(.caption)
                    .foregroundColor(.gray)
                    .fixedSize(horizontal: false, vertical: true)
            }
            Spacer()
            Menu {
                Button(MR.strings().menu_edit_process_name.desc().localized()) {
                    processListener?.onRename(id: process.processId, prevName: process.name)
                }
                Button(MR.strings().menu_export_process_string.desc().localized()) {
                    processListener?.onExportString(id: process.processId)
                }
            } label: {
                Image(systemName: "ellipsis.circle")
                    .padding()
            }
        }
        .padding(.vertical, 4)
    }
}

extension ProcessSummaryItem: Equatable {
    static func == (lhs: ProcessSummaryItem, rhs: ProcessSummaryItem) -> Bool {
        lhs.process == rhs.process
    }
}

extension ModelProcessSummary {
    private static let stringResolver = StringResolver()

    fileprivate var processDetailsText: String {
        let nodeCount = ModelProcessSummary.stringResolver.getPluralString(res: MR.plurals().node, quantity: self.nodeCount)
        return ModelProcessSummary.stringResolver.formatString(format: MR.strings().form_process_description,
                                                               args: self.amount, self.localizedName, nodeCount)
    }
}
