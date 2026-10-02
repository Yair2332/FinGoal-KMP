import SwiftUI

struct BalanceCard: View {

    @Environment(\.finGoalTheme) private var theme

    let totalBalance: Double
    let isDarkMode: Bool
    let onToggleDarkMode: () -> Void
    let onNavigateToTransactions: () -> Void

    var body: some View {
        VStack {

            HStack {

                Text("Balance Total")
                    .font(.body)
                    .foregroundStyle(.white.opacity(0.7))

                Spacer()

                Button {
                    onToggleDarkMode()
                } label: {
                    Image(
                        systemName:
                            isDarkMode
                            ? "sun.max.fill"
                            : "moon.fill"
                    )
                    .foregroundStyle(.white)
                    .font(.system(size: 20))
                }
            }

            HStack {

                Text(formatCurrency(totalBalance))
                    .font(
                        .system(
                            size: 45,
                            weight: .bold
                        )
                    )
                    .foregroundStyle(.white)

                Spacer()
            }

            Spacer()

            HStack {

                Spacer()

                Button {
                    onNavigateToTransactions()
                } label: {

                    Text("Transferir")
                        .font(
                            .system(
                                size: 16,
                                weight: .bold
                            )
                        )
                        .foregroundStyle(theme.primary)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 8)
                        .background(.white)
                        .clipShape(
                            RoundedRectangle(
                                cornerRadius: 12
                            )
                        )
                }
            }
        }
        .padding(10)
        .frame(maxWidth: .infinity)
        .frame(height: 160)
        .background(theme.primary)
        .clipShape(
            RoundedRectangle(
                cornerRadius: 24
            )
        )
    }

    private func formatCurrency(_ value: Double) -> String {
        String(
            format: "$%.0f",
            value
        )
    }
}