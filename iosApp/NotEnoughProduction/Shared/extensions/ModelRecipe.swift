//
//  ModelRecipe.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/07.
//

import Shared

extension ModelRecipe {
    var machienNameText: String {
        switch self {
        case let recipe as ModelMachineRecipeView:
            return recipe.machineName
        default:
            return MR.strings().txt_crafting_table.desc().localized()
        }
    }
}
