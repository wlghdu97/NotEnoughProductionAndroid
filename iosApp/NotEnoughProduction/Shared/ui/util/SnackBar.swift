//
//  SnackBar.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/21.
//

import SwiftUI

struct SnackBar<Presenting>: View where Presenting: View {
    @Binding var isPresented: Bool

    var message: String
    var snackBarAction: SnackBarAction?

    var dismissed: (() -> Void)?
    let presenter: () -> Presenting
    let delay: TimeInterval = 3

    var body: some View {
        if self.isPresented {
            DispatchQueue.main.asyncAfter(deadline: .now() + self.delay) {
                withAnimation {
                    self.isPresented = false
                    dismissed?.self()
                }
            }
        }

        return ZStack(alignment: .bottom) {
            presenter()
            HStack {
                Text(message)
                    .font(.subheadline)
                    .padding()
                Spacer()
                if snackBarAction != nil {
                    Button {
                        snackBarAction!.action()
                    } label: {
                        Text(snackBarAction!.actionName.uppercased())
                            .font(.headline)
                            .foregroundColor(.accentColor)
                    }
                    .padding()
                }
            }
            .background(Color(red: 0.19, green: 0.19, blue: 0.19))
            .foregroundColor(.white)
            .cornerRadius(4.0)
            .padding(4.0)
            .opacity(self.isPresented ? 1 : 0)
        }
    }
}

struct SnackBarAction {
    let actionName: String
    let action: () -> Void
}

extension View {
    func snackBar(isPresented: Binding<Bool>, message: String, action: SnackBarAction? = nil) -> some View {
        SnackBar(isPresented: isPresented, message: message, snackBarAction: action, presenter: { self })
    }

    func snackBar(isPresented: Binding<Bool>, message: String, action: SnackBarAction? = nil, onDismissed: @escaping () -> Void) -> some View {
        SnackBar(isPresented: isPresented, message: message, snackBarAction: action, dismissed: onDismissed, presenter: { self })
    }
}

struct SnackBar_Previews: PreviewProvider {
    static var previews: some View {
        SnackBar(
            isPresented: .constant(true),
            message: "Snackbar Title Here",
            snackBarAction: SnackBarAction(actionName: "dismiss", action: {}),
            presenter: { EmptyView() }
        )
    }
}
