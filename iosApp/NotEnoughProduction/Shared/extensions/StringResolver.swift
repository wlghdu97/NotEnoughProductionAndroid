//
//  StringResolver.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import Foundation
import Shared

extension StringResolver {
    static let global = StringResolver()
}

extension StringResolver {
    func formatString(format res: StringResource, args: Any...) -> String {
        let array = KotlinArray(size: Int32(args.count)) { index in
            args[index.intValue] as AnyObject
        }
        return formatString(format: res, args: array)
    }
}
