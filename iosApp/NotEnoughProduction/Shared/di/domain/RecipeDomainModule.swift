//
//  RecipeDomainModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/23.
//

import SwiftUI
import Cleanse
import Shared

struct RecipeDomainModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.bind(LoadUsageRecipeListUseCase.self)
            .to(factory: LoadUsageRecipeListUseCase.init)
        binder.bind(LoadUsageListUseCase.self)
            .to(factory: LoadUsageListUseCase.init)
        binder.bind(LoadRecipeListUseCase.self)
            .to(factory: LoadRecipeListUseCase.init)
        binder.bind(LoadUsageMachineListUseCase.self)
            .to(factory: LoadUsageMachineListUseCase.init)
        binder.bind(LoadRecipeMachineListUseCase.self)
            .to(factory: LoadRecipeMachineListUseCase.init)
    }
}
