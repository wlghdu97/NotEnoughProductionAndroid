//
//  DomainModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Cleanse

struct DomainModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.include(module: ItemDomainModule.self)
    }
}
