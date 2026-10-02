import SwiftUI

struct RegisterView: View {

    let onNavigateToLogin: () -> Void
    let onRegisterSuccess: () -> Void

    @State private var email = ""
    @State private var password = ""
    @State private var confirm = ""

    @State private var isLoading = false
    @State private var errorMessage: String?

    var body: some View {
        VStack(
            alignment: .center,
            spacing: 0
        ) {

            Spacer()

            Image(systemName: "person.badge.plus")
                .font(.system(size: 90))
                .foregroundStyle(.tint)
                .frame(width: 120, height: 120)
                .padding(.bottom, 16)

            Text("Crear cuenta")
                .font(.system(
                    size: 28,
                    weight: .heavy
                ))
                .foregroundStyle(.primary)

            Text("Comienza tu aventura financiera hoy")
                .foregroundStyle(.gray)
                .padding(.bottom, 32)

            AuthTextField(
                value: $email,
                label: "Correo",
                systemImage: "envelope"
            )

            Spacer()
                .frame(height: 12)

            AuthTextField(
                value: $password,
                label: "Contraseña",
                systemImage: "lock",
                isPassword: true
            )

            Spacer()
                .frame(height: 12)

            AuthTextField(
                value: $confirm,
                label: "Confirmar Contraseña",
                systemImage: "lock",
                isPassword: true
            )

            Spacer()
                .frame(height: 24)

            AuthButton(
                text: "Registrarse",
                isLoading: isLoading
            ) {
                register()
            }

            Button {
                onNavigateToLogin()
            } label: {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
            .buttonStyle(.plain)
            .foregroundStyle(.tint)

            if let errorMessage {
                Text(errorMessage)
                    .foregroundStyle(.red)
                    .font(.footnote)
                    .padding(.top, 12)
            }

            Spacer()
        }
        .padding(24)
    }

    private func register() {
        // Acá conectaremos:
        // viewModel.register(email, password, confirm)
        // cuando conectemos KMP con Swift.

        print(
            "Register:",
            email,
            password,
            confirm
        )
    }
}