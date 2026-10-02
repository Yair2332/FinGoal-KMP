import SwiftUI

struct LoginView: View {

    let onNavigateToRegister: () -> Void
    let onLoginSuccess: () -> Void

    @State private var email = ""
    @State private var password = ""

    @State private var isLoading = false
    @State private var errorMessage: String?

    var body: some View {
        VStack(
            alignment: .center,
            spacing: 0
        ) {

            Spacer()

            Image("fingoal")
                .resizable()
                .scaledToFit()
                .frame(width: 180, height: 180)
                .padding(.bottom, 24)

            Text("Bienvenido")
                .font(.system(
                    size: 28,
                    weight: .bold
                ))
                .foregroundStyle(.primary)

            Text("Inicia sesión para continuar")
                .foregroundStyle(.gray)
                .padding(.bottom, 32)

            AuthTextField(
                value: $email,
                label: "Correo",
                systemImage: "envelope"
            )

            Spacer()
                .frame(height: 16)

            AuthTextField(
                value: $password,
                label: "Contraseña",
                systemImage: "lock",
                isPassword: true
            )

            Spacer()
                .frame(height: 32)

            AuthButton(
                text: "Entrar",
                isLoading: isLoading
            ) {
                login()
            }

            Button {
                onNavigateToRegister()
            } label: {
                Text("¿No tienes cuenta? Regístrate")
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

    private func login() {
        // Acá conectaremos AuthViewModel.login(...)
        // cuando hagamos el puente KMP → Swift.

        print("Login:", email)
    }
}