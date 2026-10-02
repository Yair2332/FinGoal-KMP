import SwiftUI

struct InfoCard: View {

    @Environment(\.finGoalTheme) private var theme

    let title: String
    let value: String
    let systemImage: String
    let iconTint: Color

    var body: some View {
        HStack {

            ZStack {
                RoundedRectangle(cornerRadius: 8)
                    .fill(iconTint.opacity(0.15))

                Image(systemName: systemImage)
                    .font(.system(size: 24))
                    .foregroundStyle(iconTint)
            }
            .frame(width: 40, height: 40)

            Spacer()
                .frame(width: 12)

            VStack(
                alignment: .leading,
                spacing: 2
            ) {
                Text(title)
                    .font(.caption)
                    .foregroundStyle(theme.onSurfaceVariant)

                Text(value)
                    .font(
                        .system(
                            size: 18,
                            weight: .bold
                        )
                    )
                    .foregroundStyle(iconTint)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity)
        .background(theme.surfaceVariant)
        .clipShape(
            RoundedRectangle(cornerRadius: 16)
        )
        .shadow(
            color: .black.opacity(0.08),
            radius: 2
        )
    }
}