import SwiftUI
import SharedLogic

@main
struct iosApp: App {

    init() {
        initializeKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}