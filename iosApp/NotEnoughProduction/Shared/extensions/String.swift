//
//  String.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import Foundation
import Shared

extension String {
    func unnamedIfEmpty() -> String {
        if self.isEmpty {
            return MR.strings().txt_unnamed.desc().localized()
        } else {
            return self
        }
    }
}
