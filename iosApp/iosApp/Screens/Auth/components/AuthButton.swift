import SwiftUI

struct AuthButton: View {
    let text: String
    let isLoading: Bool
    let onClick: () -> Void

    var body: some View {
        Button {
            onClick()
        } label: {
            Group {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(
                            CircularProgressViewStyle(tint: .white)
                        )
                } else {
                    Text(text)
                        .font(
                            .system(
                                size: 16,
                                weight: .semibold
                            )
                        )
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 50)
        }
        .foregroundStyle(.white)
        .background(
            Color.accentColor
        )
        .clipShape(
            RoundedRectangle(cornerRadius: 12)
        )
        .disabled(isLoading)
    }
}