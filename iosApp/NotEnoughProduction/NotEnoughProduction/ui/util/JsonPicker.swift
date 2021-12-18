//
//  JsonPicker.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/12/02.
//

import SwiftUI
import UIKit

struct JsonPicker: UIViewControllerRepresentable {
    @Binding var data: Data?
    let onCancellation: () -> Void

    func makeCoordinator() -> JsonPicker.Coordinator {
        JsonPicker.Coordinator(self)
    }

    func makeUIViewController(context: UIViewControllerRepresentableContext<JsonPicker>) -> UIDocumentPickerViewController {
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: [.json])
        picker.allowsMultipleSelection = false
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: JsonPicker.UIViewControllerType, context: UIViewControllerRepresentableContext<JsonPicker>) { }

    class Coordinator: NSObject, UIDocumentPickerDelegate {
        var parent: JsonPicker

        init(_ parent: JsonPicker) {
            self.parent = parent
        }

        func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            guard let url = urls.first else {
                return
            }
            guard url.startAccessingSecurityScopedResource() else {
                debugPrint("Failed to access security scoped resource : \(url)")
                return
            }

            defer { url.stopAccessingSecurityScopedResource() }

            debugPrint("accessing : \(url.absoluteString)")
            do {
                parent.data = try Data(contentsOf: url, options: [.alwaysMapped, .uncached])
            } catch {
                debugPrint(error)
                parent.onCancellation()
            }
        }

        func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
            parent.onCancellation()
        }
    }
}
