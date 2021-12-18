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
    let withIcon: Bool

    var body: some View {
        if let listener = elementListener {
            Button {
                listener.onClick(elementId: element.id)
            } label: {
                ElementItem(element: element, withIcon: withIcon)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .padding(.vertical, 4)
        } else {
            ElementItem(element: element, withIcon: withIcon)
        }
    }
}

extension RecipeViewElementItem {
    private struct ElementItem: View {
        let element: ModelRecipeElement
        let withIcon: Bool

        @ViewBuilder
        var body: some View {
            HStack {
                RecipeElementItem(element: element, withAmount: true, withIcon: withIcon)
                Spacer()
                Image(systemName: "chevron.right")
                    .resizable()
                    .frame(width: 8, height: 12)
                    .font(Font.system(size: 60, weight: .semibold))
                    .foregroundColor(Color(UIColor.tertiaryLabel))
                    .padding(4)
            }
        }
    }
}

extension RecipeViewElementItem: Equatable {
    static func == (lhs: RecipeViewElementItem, rhs: RecipeViewElementItem) -> Bool {
        lhs.element == rhs.element
    }
}
