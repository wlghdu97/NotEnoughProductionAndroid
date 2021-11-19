//
//  ItemDomainModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Cleanse
import Shared

struct ItemDomainModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.bind(ElementSearchUseCase.self)
            .to(factory: ElementSearchUseCase.init)
        binder.bind(LoadElementDetailWithKeyUseCase.self)
            .to(factory: LoadElementDetailWithKeyUseCase.init)
        binder.bind(CheckReplacementListCountUseCase.self)
            .to(factory: CheckReplacementListCountUseCase.init)
        binder.bind(LoadOreDictListUseCase.self)
            .to(factory: LoadOreDictListUseCase.init)
        binder.bind(LoadElementDetailUseCase.self)
            .to(factory: LoadElementDetailUseCase.init)
        binder.bind(LoadReplacementListUseCase.self)
            .to(factory: LoadReplacementListUseCase.init)
    }
}
