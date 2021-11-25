//
//  RecipeViewElementItem.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import SwiftUI
import Shared

struct RecipeViewElementItem: View {
    let element: ModelRecipeElement
    let elementListener: ElementListener?

    var body: some View {
        Button {
            elementListener?.onClick(elementId: element.id)
        } label: {
            HStack {
                RecipeElementItem(element: element, withAmount: true)
                Spacer()
                Image(systemName: "chevron.right")
                    .resizable()
                    .frame(width: 8, height: 12)
                    .font(Font.system(size: 60, weight: .semibold))
                    .foregroundColor(Color(UIColor.tertiaryLabel))
                    .padding(4)
            }
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .padding(.vertical, 4)
    }
}

extension RecipeViewElementItem: Equatable {
    static func == (lhs: RecipeViewElementItem, rhs: RecipeViewElementItem) -> Bool {
        lhs.element == rhs.element
    }
}
