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
    let withAmount: Bool

    init (element: ModelRecipeElement, withAmount: Bool = false) {
        self.element = element
        self.withAmount = withAmount
    }

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(localizedNameText)
                    .font(.body)
                Text(element.unlocalizedName.unnamedIfEmpty())
                    .captionText()
                    .lineLimit(1)
            }
            Spacer()
            Text(element.elementTypeText)
                .captionText()
        }
        .padding(.vertical, 4)
    }
}

extension RecipeElementItem {
    fileprivate var localizedNameText: String {
        if withAmount {
            return StringResolver.global.formatString(format: MR.strings().form_item_with_amount, args: NumberFormatter.global.string(from: NSNumber(value: element.amount)) ?? "0", element.localizedNameText)
        } else {
            return element.localizedNameText
        }
    }
}
