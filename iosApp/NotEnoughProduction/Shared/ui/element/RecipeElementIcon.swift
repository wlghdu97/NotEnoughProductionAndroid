//
//  RecipeElementIcon.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/26.
//

import SwiftUI
import Shared

struct RecipeElementIcon: View {
    let element: ModelRecipeElement

    var body: some View {
        Group {
            if let image = element.image {
                Image(uiImage: image)
                    .resizable()
                    .cornerRadius(8)
            } else {
                Rectangle()
                    .fill(Color.clear)
            }
        }
    }
}
