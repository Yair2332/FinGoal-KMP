import SwiftUI
import SharedLogic

struct ContentView: UIViewControllerRepresentable {

    func makeUIViewController(
        context: Context
    ) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(
        _ uiViewController: UIViewController,
        context: Context
    ) {
    }
}