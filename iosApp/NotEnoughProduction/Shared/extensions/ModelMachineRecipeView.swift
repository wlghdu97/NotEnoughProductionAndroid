//
//  ModelMachineRecipeView.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import Shared
import Foundation

extension ModelMachineRecipeView {
    var powerTypeText: String {
        switch self.powerType {
        case 0:
            return MR.strings().txt_eu.desc().localized()
        case 1:
            return MR.strings().txt_rf.desc().localized()
        case 2:
            return MR.strings().txt_fuel.desc().localized()
        default:
            return MR.strings().txt_unknown.desc().localized()
        }
    }

    var detailsText: String {
        let unit = powerTypeText
        let unitTick = "\(unit)\(MR.strings().txt_per_tick.desc().localized())"
        let durationSec = Double(duration) / 20.0
        let total = Int64(ept) * Int64(duration)
        return StringResolver.global.formatString(format: MR.strings().form_machine_property,
                                                  args: NumberFormatter.global.string(from: NSNumber(value: ept)) ?? "0", unitTick, NumberFormatter.global.string(from: NSNumber(value: durationSec)) ?? "0", NumberFormatter.global.string(from: NSNumber(value: total)) ?? "0", unit)
    }
}
