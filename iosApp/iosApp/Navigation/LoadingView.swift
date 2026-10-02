import SwiftUI

struct LoadingView: View {

    var body: some View {
        ZStack {
            Color(uiColor: .systemBackground)
                .ignoresSafeArea()

            ProgressView()
                .progressViewStyle(.circular)
        }
    }
}