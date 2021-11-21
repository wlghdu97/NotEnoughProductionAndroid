//
//  ProcessDomainModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import Foundation
import Cleanse
import Shared

struct ProcessDomainModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.bind(ExportProcessStringUseCase.self)
            .to(factory: ExportProcessStringUseCase.init)
        binder.bind(LoadProcessUseCase.self)
            .to(factory: LoadProcessUseCase.init)
        binder.bind(ImportProcessStringUseCase.self)
            .to(factory: ImportProcessStringUseCase.init)
        binder.bind(LoadProcessListUseCase.self)
            .to(factory: LoadProcessListUseCase.init)
    }
}
