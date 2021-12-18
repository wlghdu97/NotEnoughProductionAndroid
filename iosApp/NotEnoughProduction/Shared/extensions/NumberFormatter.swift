//
//  NumberFormatter.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/25.
//

import Foundation

extension NumberFormatter {
    static let global = NumberFormatter().apply { formatter in
        formatter.numberStyle = .decimal
    }
}
