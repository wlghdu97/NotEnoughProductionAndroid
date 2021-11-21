//
//  TextFieldContainer.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/20.
//

import SwiftUI
import UIKit

struct TextFieldContainer: UIViewRepresentable {
    typealias UIViewType = UITextField

    private var placeholder: String
    private var text: Binding<String>
    private var focused: Binding<Bool>?
    private var textAlignment: NSTextAlignment = .natural

    init(_ placeholder: String, text: Binding<String>, isFocused: Binding<Bool>? = nil) {
        self.placeholder = placeholder
        self.text = text
        self.focused = isFocused
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIView(context: Context) -> UITextField {
        let innerTextField = UITextField(frame: .zero)
        innerTextField.placeholder = placeholder
        innerTextField.text = text.wrappedValue
        innerTextField.delegate = context.coordinator
        context.coordinator.initTextField(innerTextField)

        return innerTextField
    }

    func updateUIView(_ uiView: UITextField, context: Context) {
        uiView.textAlignment = textAlignment
        if uiView.text == "" && text.wrappedValue.count > 0 {
            uiView.text = text.wrappedValue
        }
        if self.focused?.wrappedValue == true {
            uiView.becomeFirstResponder()
        }
    }

    class Coordinator: NSObject, UITextFieldDelegate {
        private var parent: TextFieldContainer

        init(_ textFieldContainer: TextFieldContainer) {
            self.parent = textFieldContainer
        }

        func initTextField(_ textField: UITextField) {
            textField.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
        }

        @objc func textFieldDidChange(_ textField: UITextField) {
            self.parent.text.wrappedValue = textField.text ?? ""
        }

        func textField(_ textField: UITextField,
                       shouldChangeCharactersIn range: NSRange,
                       replacementString string: String) -> Bool {
            return true
        }

        func textFieldDidBeginEditing(_ textField: UITextField) {
            DispatchQueue.main.async {
                let newPosition = textField.endOfDocument
                textField.selectedTextRange = textField.textRange(from: newPosition, to: newPosition)
            }
        }

        func textFieldDidEndEditing(_ textField: UITextField) {
            self.parent.text.wrappedValue = textField.text ?? ""
        }
    }
}

extension TextFieldContainer {
    func textAlignment(_ alignment: NSTextAlignment) -> TextFieldContainer {
        var view = self
        view.textAlignment = alignment
        return view
    }
}
