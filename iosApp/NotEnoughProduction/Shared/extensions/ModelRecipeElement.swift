//
//  ModelRecipeElement.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Shared
import UIKit

extension ModelRecipeElement: Identifiable { }

extension ModelRecipeElement {
    var localizedNameText: String {
        let localizedName = localizedName.unnamedIfEmpty().trimmingCharacters(in: .whitespacesAndNewlines)
        if let metaData = metaData, !metaData.isEmpty {
            return localizedName + " : " + metaData
        } else {
            return localizedName
        }
    }

    var elementTypeText: String {
        switch self.type {
        case ModelElement.companion.ITEM:
            return MR.strings().txt_item.desc().localized()
        case ModelElement.companion.FLUID:
            return MR.strings().txt_fluid.desc().localized()
        case ModelElement.companion.ORE_DICT:
            return MR.strings().txt_ore_dict.desc().localized()
        case ModelElement.companion.ORE_CHAIN:
            return MR.strings().txt_ore_chain_recipe.desc().localized()
        default:
            return MR.strings().txt_unknown.desc().localized()
        }
    }
}

extension ModelRecipeElement {
    var image: UIImage? {
        if let iconDirUrl = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first {
            let fileName = Data(unlocalizedName.utf8).base64EncodedString()
            let iconUrl = iconDirUrl.appendingPathComponent("icons/\(fileName).png")
            do {
                let data = try Data(contentsOf: iconUrl)
                return UIImage(data: data)
            } catch {
                print(error)
                return nil
            }
        } else {
            return nil
        }
    }
}
