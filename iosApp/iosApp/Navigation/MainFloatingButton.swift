import SwiftUI

struct MainFloatingButton: View {

    @Environment(\.finGoalTheme) private var theme

    let onClick: () -> Void

    var body: some View {
        Button {
            onClick()
        } label: {
            Image(systemName: "plus")
                .font(.system(size: 24, weight: .medium))
                .foregroundStyle(.white)
                .frame(width: 70, height: 70)
                .background(
                    theme.primary,
                    in: Circle()
                )
        }
        .buttonStyle(.plain)
        .shadow(
            color: .black.opacity(0.20),
            radius: 5,
            y: 3
        )
    }
}