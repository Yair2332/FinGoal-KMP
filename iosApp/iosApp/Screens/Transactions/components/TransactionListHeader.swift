import SwiftUI

struct TransactionListHeader: View {
    let ingresos: Double
    let gastos: Double

    var body: some View {
        VStack(spacing: 0) {
            PeriodoActual()

            Spacer()
                .frame(height: 8)

            ResumenFinanciero(
                ingresos: ingresos,
                gastos: gastos
            )
        }
        .frame(maxWidth: .infinity)
    }
}