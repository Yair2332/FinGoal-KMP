import SwiftUI

struct AuthNavigation: View {

    @State private var showRegister = false

    let onAuthenticated: () -> Void

    var body: some View {
        Group {
            if showRegister {

                RegisterView(
                    onNavigateToLogin: {
                        showRegister = false
                    },
                    onRegisterSuccess: {
                        onAuthenticated()
                    }
                )

            } else {

                LoginView(
                    onNavigateToRegister: {
                        showRegister = true
                    },
                    onLoginSuccess: {
                        onAuthenticated()
                    }
                )
            }
        }
    }
}