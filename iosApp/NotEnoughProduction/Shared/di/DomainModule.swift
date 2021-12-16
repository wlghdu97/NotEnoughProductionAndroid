//
//  DomainModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//
//  swiftlint:disable closure_parameter_position
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

        binder.bind(ParseRecipeUseCase.self)
            .to { (machineRecipeParser: MachineRecipeParser,
                   shapedRecipeParser: ShapedRecipeParser,
                   shapelessRecipeParser: ShapelessRecipeParser,
                   shapedOreRecipeParser: ShapedOreRecipeParser,
                   shapelessOreRecipeParser: ShapelessOreRecipeParser,
                   replacementListParser: ReplacementListParser,
                   furnaceRecipeParser: FurnaceRecipeParser,
                   elementRepo: ElementRepo,
                   machineRepo: MachineRepo,
                   preference: GeneralPreference) in
                return ParseRecipeUseCase(machineRecipeParser: machineRecipeParser,
                                          shapedRecipeParser: shapedRecipeParser,
                                          shapelessRecipeParser: shapelessRecipeParser,
                                          shapedOreRecipeParser: shapedOreRecipeParser,
                                          shapelessOreRecipeParser: shapelessOreRecipeParser,
                                          replacementListParser: replacementListParser,
                                          furnaceRecipeParser: furnaceRecipeParser,
                                          elementRepo: elementRepo,
                                          machineRepo: machineRepo,
                                          generalPreference: preference,
                                          io: Dispatchers().default_)
            }

        binder.bind(IconUnzipUseCase.self)
            .to { (preference: GeneralPreference, stringResolver: StringResolver) in
                let applicationSupportDir = File(fileURLWithPath: FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first!.path)
                let outputDir = File(parent: applicationSupportDir, child: "icons")
                return IconUnzipUseCase(generalPreference: preference, stringResolver: stringResolver, outputDir: outputDir, io: Dispatchers().default_)
            }
    }
}
