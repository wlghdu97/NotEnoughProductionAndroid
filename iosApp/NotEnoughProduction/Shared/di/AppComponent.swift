//
//  AppComponent.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/19.
//

import Foundation
import Cleanse

struct AppComponent: Cleanse.RootComponent {
    typealias Root = PropertyInjector<NotEnoughProductionApp>
    typealias Scope = Singleton

    static func configure(binder: Binder<Singleton>) {
        binder.include(module: AppModule.self)
        binder.include(module: DatabaseModule.self)
        binder.include(module: DomainModule.self)
        binder.include(module: UIModule.self)
    }

    static func configureRoot(binder bind: ReceiptBinder<PropertyInjector<NotEnoughProductionApp>>) -> BindingReceipt<PropertyInjector<NotEnoughProductionApp>> {
        return bind.propertyInjector { (bind) -> BindingReceipt<PropertyInjector<NotEnoughProductionApp>> in
            bind.to(injector: NotEnoughProductionApp.injectProperties)
        }
    }
}
