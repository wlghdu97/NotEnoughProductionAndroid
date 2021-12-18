//
//  SwiftUIViewModel.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Cleanse
import Shared

class SwiftUIViewModel<VM: ViewModel> {
    internal var viewModel: VM!

    init (viewModel: VM) {
        self.viewModel = viewModel
    }

    init () {
        self.viewModel = nil
    }
}
