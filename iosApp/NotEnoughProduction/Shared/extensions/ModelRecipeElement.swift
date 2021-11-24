//
//  ModelRecipeElement.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Shared

extension ModelRecipeElement: Identifiable { }

extension ModelRecipeElement {
    var elementTypeText: String {
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
