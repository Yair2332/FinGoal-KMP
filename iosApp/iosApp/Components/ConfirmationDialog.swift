import SwiftUI

struct ConfirmationDialogView: View {
    let title: String
    let message: String
    let confirmButtonText: String
    let dismissButtonText: String
    let onConfirm: () -> Void
    let onDismiss: () -> Void

    init(
        title: String,
        message: String,
        confirmButtonText: String = "Confirmar",
        dismissButtonText: String = "Cancelar",
        onConfirm: @escaping () -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.title = title
        self.message = message
        self.confirmButtonText = confirmButtonText
        self.dismissButtonText = dismissButtonText
        self.onConfirm = onConfirm
        self.onDismiss = onDismiss
    }

    var body: some View {
        EmptyView()
    }
}