//
//  HasApply.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import Foundation

protocol HasApply { }

extension HasApply {
    func apply(block: (Self) -> Void) -> Self {
        block(self)
        return self
    }
}

extension NSObject: HasApply { }
