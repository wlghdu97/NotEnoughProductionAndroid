//
//  MachineItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import SwiftUI
import Shared

struct MachineItem: View, Equatable {
    let machine: ModelMachine

    var body: some View {
        VStack(alignment: .leading) {
            Text(machine.name.unnamedIfEmpty())
                .font(.body)
            Text(machine.modName.unnamedIfEmpty())
                .font(.caption)
                .foregroundColor(.gray)
                .lineLimit(1)
        }
        .padding(.vertical, 4)
    }
}
