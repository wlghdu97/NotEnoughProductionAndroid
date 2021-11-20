//
//  RecipeElementItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import SwiftUI
import Shared

struct RecipeElementItem: View, Equatable {
    let element: ModelRecipeElement

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(element.localizedName.unnamedIfEmpty())
                    .font(.body)
                Text(element.unlocalizedName.unnamedIfEmpty())
                    .font(.caption)
                    .foregroundColor(.gray)
                    .lineLimit(1)
            }
            Spacer()
            Text(element.elementTypeText)
                .font(.caption)
                .foregroundColor(.gray)
        }
        .padding(.vertical, 4)
    }
}

extension ModelRecipeElement {
    fileprivate var elementTypeText: String {
        switch self.type {
        case ModelElement.companion.ITEM:
            return MR.strings().txt_item.desc().localized()
        case ModelElement.companion.FLUID:
            return MR.strings().txt_fluid.desc().localized()
        default:
            return MR.strings().txt_unknown.desc().localized()
        }
    }
}
