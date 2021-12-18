//
//  Modifiers.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import SwiftUI

extension View {
    func listCardBackground() -> some View {
        modifier(ListCardBackground())
    }
}

struct ListCardBackground: ViewModifier {
    func body(content: Content) -> some View {
        content.padding()
            .background(Color(UIColor.secondarySystemBackground))
            .cornerRadius(10)
            .padding(.horizontal)
    }
}
