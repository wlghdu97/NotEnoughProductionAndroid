//
//  ContentView.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/10/06.
//

import SwiftUI
import Shared

struct ContentView: View {
    private static let stringResolver = StringResolver()

    var body: some View {
        List {
            Text(MR.strings().title_parsing_notification.desc().localized())
            Text(ContentView.stringResolver.formatString(format: MR.strings().title_delete_process, args: "process name"))
            Text(ContentView.stringResolver.formatString(format: MR.strings().form_matched_total, args: 1))
            Text(ContentView.stringResolver.formatString(format: MR.strings().form_processing_order, args: 10, "result"))
            Text(MR.strings().txt_db_not_loaded.desc().localized())
        }
    }
}

extension StringResolver {
    func formatString(format res: StringResource, args: Any...) -> String {
        let array = KotlinArray(size: Int32(args.count)) { index in
            args[index.intValue] as AnyObject
        }
        return formatString(format: res, args: array)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
