import SwiftUI
import SharedUI

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