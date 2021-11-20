//
//  AppModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//
//  swiftlint:disable identifier_name
//

import Foundation
import Cleanse
import Shared

struct AppModule: Cleanse.Module {
    static func configure(binder: Binder<Singleton>) {
        binder.bind(StringResolver.self)
            .sharedInScope()
            .to(factory: StringResolver.init)

        binder.bind(GeneralPreference.self)
            .sharedInScope()
            .to(factory: GeneralUserDefaultsPreference.init)

        binder.bind(ElementRepo.self)
            .sharedInScope()
            .to { (db: Nep) in
                ElementRepoImpl(db: db, io: Dispatchers().default_)
            }
        binder.bind(MachineRepo.self)
            .sharedInScope()
            .to { (db: Nep) in
                MachineRepoImpl(db: db, io: Dispatchers().default_)
            }
    }
}
