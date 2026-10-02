import SwiftUI

struct AuthTextField: View {
    @Binding var value: String

    let label: String
    let systemImage: String?
    let isPassword: Bool

    init(
        value: Binding<String>,
        label: String,
        systemImage: String? = nil,
        isPassword: Bool = false
    ) {
        self._value = value
        self.label = label
        self.systemImage = systemImage
        self.isPassword = isPassword
    }

    var body: some View {
        Group {
            if isPassword {
                SecureField(
                    label,
                    text: $value
                )
            } else {
                TextField(
                    label,
                    text: $value
                )
                .keyboardType(.emailAddress)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
            }
        }
        .textFieldStyle(.plain)
        .padding(.horizontal, 12)
        .frame(height: 50)
        .background(
            Color(uiColor: .systemBackground)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(
                    Color.secondary.opacity(0.5),
                    lineWidth: 1
                )
        )
        .overlay(alignment: .leading) {
            if let systemImage {
                Image(systemName: systemImage)
                    .foregroundStyle(.secondary)
                    .padding(.leading, 12)
            }
        }
        .padding(.leading, systemImage != nil ? 28 : 0)
    }
}