import SwiftUI

struct ResumenFinanciero: View {
    @Environment(\.finGoalTheme) private var theme

    let ingresos: Double
    let gastos: Double

    private let incomeColor = Color(
        red: 76 / 255,
        green: 175 / 255,
        blue: 80 / 255
    )

    private let expenseColor = Color(
        red: 229 / 255,
        green: 57 / 255,
        blue: 53 / 255
    )

    var body: some View {
        HStack(spacing: 16) {
            financialCard(
                title: "Ingresos",
                amount: ingresos,
                icon: "arrow.upward",
                color: incomeColor
            )

            financialCard(
                title: "Gastos",
                amount: gastos,
                icon: "arrow.downward",
                color: expenseColor
            )
        }
        .padding(.vertical, 8)
    }

    @ViewBuilder
    private func financialCard(
        title: String,
        amount: Double,
        icon: String,
        color: Color
    ) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: icon)
                    .foregroundStyle(color)

                Text(title)
                    .foregroundStyle(color)
                    .fontWeight(.bold)
            }

            Text(formatAmount(amount))
                .font(.system(size: 20, weight: .bold))
                .foregroundStyle(color)
        }
        .padding(16)
        .frame(
            maxWidth: .infinity,
            alignment: .leading
        )
        .background(color.opacity(0.15))
        .clipShape(
            RoundedRectangle(cornerRadius: 12)
        )
    }

    private func formatAmount(_ amount: Double) -> String {
        String(format: "$%.2f", amount)
    }
}