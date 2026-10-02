import SwiftUI

struct DashboardSummaryCards: View {
    let totalExpenses: Double
    let savingsTotal: Double

    var body: some View {
        HStack(spacing: 16) {

            InfoCard(
                title: "Gastos Mes",
                value: formatCurrency(totalExpenses),
                systemImage: "wallet.pass",
                iconTint: Color(
                    red: 211 / 255,
                    green: 47 / 255,
                    blue: 47 / 255
                )
            )

            InfoCard(
                title: "Ahorro Total",
                value: formatCurrency(savingsTotal),
                systemImage: "banknote",
                iconTint: Color(
                    red: 56 / 255,
                    green: 142 / 255,
                    blue: 60 / 255
                )
            )
        }
    }

    private func formatCurrency(_ value: Double) -> String {
        String(
            format: "$%.0f",
            value
        )
    }
}