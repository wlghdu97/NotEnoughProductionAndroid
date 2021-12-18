//
//  DocumentPicker.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/29.
//

import SwiftUI
import UIKit
import ZIPFoundation

struct ArchivePicker: UIViewControllerRepresentable {
    @Binding var archive: Archive?
    let onCancellation: () -> Void

    func makeCoordinator() -> ArchivePicker.Coordinator {
        ArchivePicker.Coordinator(self)
    }

    func makeUIViewController(context: UIViewControllerRepresentableContext<ArchivePicker>) -> UIDocumentPickerViewController {
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: [.archive])
        picker.allowsMultipleSelection = false
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: ArchivePicker.UIViewControllerType, context: UIViewControllerRepresentableContext<ArchivePicker>) { }

    class Coordinator: NSObject, UIDocumentPickerDelegate {
        var parent: ArchivePicker

        init(_ parent: ArchivePicker) {
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
            parent.archive = Archive(url: url, accessMode: .read)
        }

        func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
            parent.onCancellation()
        }
    }
}
