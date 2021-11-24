//
//  RecipeMachineViewItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/24.
//

import SwiftUI
import Shared

struct RecipeMachineViewItem: View, Equatable {
    let view: ModelRecipeMachineView

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(view.machineName.unnamedIfEmpty())
                    .font(.body)
                Text(view.modName.unnamedIfEmpty())
                    .font(.caption)
                    .foregroundColor(.gray)
                    .lineLimit(1)
            }
            Spacer()
            Text("\(view.recipeCount)")
                .font(.caption)
                .foregroundColor(.gray)
        }
        .padding(.vertical, 4)
    }
}
