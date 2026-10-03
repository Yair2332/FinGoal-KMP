import SwiftUI
import SharedLogic

@main
struct iosApp: App {

    init() {
        initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}