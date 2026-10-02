import SwiftUI

struct MainTopBar: View {

    @Environment(\.finGoalTheme) private var theme

    let title: String
    let showHelp: Bool

    let onHelp: () -> Void
    let onLogout: () -> Void

    var body: some View {
        HStack {

            HStack(spacing: 8) {

                Image("logo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 42, height: 42)

                Text(title)
                    .font(.system(size: 20, weight: .bold))
                    .foregroundStyle(theme.primary)
            }

            Spacer()

            if showHelp {

                Button {
                    onHelp()
                } label: {
                    Image(systemName: "questionmark")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(.white)
                        .frame(width: 26, height: 26)
                        .background(
                            theme.primary,
                            in: Circle()
                        )
                }
                .buttonStyle(.plain)

            } else {

                Button {
                    onLogout()
                } label: {
                    Image(
                        systemName: "rectangle.portrait.and.arrow.right"
                    )
                    .font(.system(size: 20))
                    .foregroundStyle(theme.primary)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 16)
        .frame(height: 56)
        .background(
            theme.background
        )
        .shadow(
            color: .black.opacity(0.12),
            radius: 2,
            y: 2
        )
    }
}