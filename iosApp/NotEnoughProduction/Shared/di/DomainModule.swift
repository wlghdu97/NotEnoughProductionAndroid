//
//  DomainModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Cleanse
import Shared

struct DomainModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.include(module: ItemDomainModule.self)
        binder.include(module: RecipeDomainModule.self)
        binder.include(module: MachineDomainModule.self)
        binder.include(module: ProcessDomainModule.self)

        binder.bind(IconUnzipUseCase.self)
            .to { (preference: GeneralPreference, stringResolver: StringResolver) in
                let applicationSupportDir = File(fileURLWithPath: FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first!.path)
                let outputDir = File(parent: applicationSupportDir, child: "icons")
                return IconUnzipUseCase(generalPreference: preference, stringResolver: stringResolver, outputDir: outputDir, io: Dispatchers().default_)
            }
    }
}
