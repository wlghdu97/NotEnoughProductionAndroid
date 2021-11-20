//
//  MachineDomainModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import Foundation
import Cleanse
import Shared

struct MachineDomainModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.bind(LoadMachineUseCase.self)
            .to(factory: LoadMachineUseCase.init)
        binder.bind(MachineResultSearchUseCase.self)
            .to(factory: MachineResultSearchUseCase.init)
        binder.bind(LoadMachineListUseCase.self)
            .to(factory: LoadMachineListUseCase.init)
    }
}
