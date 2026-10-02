import SwiftUI

struct ContentView: View {

    @State private var appState: AppState = .loading

    enum AppState {
        case loading
        case auth
        case main
    }

    var body: some View {
        Group {
            switch appState {

            case .loading:
                LoadingView()

            case .auth:
                AuthNavigation(
                    onAuthenticated: {
                        appState = .main
                    }
                )

            case .main:
                MainAppNavigation(
                    onLogout: {
                        appState = .auth
                    }
                )
            }
        }
    }
}