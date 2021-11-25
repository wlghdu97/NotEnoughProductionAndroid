//
//  CaptionText.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import SwiftUI

extension View {
    func captionText() -> some View {
        modifier(CaptionText())
    }
}

struct CaptionText: ViewModifier {
    func body(content: Content) -> some View {
        content
            .font(.caption)
            .foregroundColor(Color(UIColor.secondaryLabel))
    }
}
