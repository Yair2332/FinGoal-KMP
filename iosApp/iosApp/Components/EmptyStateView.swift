import SwiftUI

struct EmptyStateView: View {
    let message: String

    @State private var arrowOffset: CGFloat = 0

    var body: some View {
        VStack {
            Text(message)
                .font(.body)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .lineSpacing(4)

            Spacer()
                .frame(height: 60)

            Image(systemName: "arrow.down")
                .font(.system(size: 40))
                .foregroundStyle(.tint)
                .offset(y: arrowOffset)
                .onAppear {
                    withAnimation(
                        .easeInOut(duration: 1)
                        .repeatForever(autoreverses: true)
                    ) {
                        arrowOffset = 15
                    }
                }
        }
        .frame(maxWidth: .infinity)
        .padding(24)
    }
}