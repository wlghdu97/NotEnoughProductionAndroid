//
//  DatabaseModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Cleanse
import Shared

struct DatabaseModule: Cleanse.Module {
    static func configure(binder: Binder<Singleton>) {
        binder.bind(Nep.self)
            .sharedInScope()
            .to(value: DatabaseKt.nep)
        binder.bind(NepProcess.self)
            .sharedInScope()
            .to(value: DatabaseKt.nepProcess)
    }
}
