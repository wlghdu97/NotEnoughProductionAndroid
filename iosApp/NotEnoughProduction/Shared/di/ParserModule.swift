//
//  ParserModule.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/15.
//

import Foundation
import Cleanse
import Shared

struct ParserModule: Cleanse.Module {
    static func configure(binder: Binder<Unscoped>) {
        binder.bind(FluidParser.self)
            .to(factory: FluidParser.init)
        binder.bind(ItemParser.self)
            .to(factory: ItemParser.init)
        binder.bind(VanillaItemParser.self)
            .to(factory: VanillaItemParser.init)

        binder.bind(OreDictItemParser.self)
            .to(factory: OreDictItemParser.init)
        binder.bind(ReplacementParser.self)
            .to(factory: ReplacementParser.init)

        binder.bind(FurnaceRecipeParser.self)
            .to(factory: FurnaceRecipeParser.init)
        binder.bind(MachineRecipeParser.self)
            .to(factory: MachineRecipeParser.init)
        binder.bind(ReplacementListParser.self)
            .to(factory: ReplacementListParser.init)
        binder.bind(ShapedOreRecipeParser.self)
            .to(factory: ShapedOreRecipeParser.init)
        binder.bind(ShapedRecipeParser.self)
            .to(factory: ShapedRecipeParser.init)
        binder.bind(ShapelessOreRecipeParser.self)
            .to(factory: ShapelessOreRecipeParser.init)
        binder.bind(ShapelessRecipeParser.self)
            .to(factory: ShapelessRecipeParser.init)
    }
}
