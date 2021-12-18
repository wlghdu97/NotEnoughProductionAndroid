//
//  ModelRecipeView.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import Shared

extension ModelRecipeView: Identifiable { }

extension ModelRecipeView {
    func getTitle(_ targetElement: ModelRecipeElement) -> String {
        StringResolver.global.formatString(format: MR.strings().form_item_with_amount, args: NumberFormatter.global.string(from: NSNumber(value: targetElement.amount)) ?? "0", targetElement.localizedName.unnamedIfEmpty())
    }

    var machienNameText: String {
        (self as ModelRecipe).machienNameText
    }
}
