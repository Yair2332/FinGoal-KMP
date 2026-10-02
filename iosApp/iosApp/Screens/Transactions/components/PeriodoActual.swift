import SwiftUI

struct PeriodoActual: View {
    @Environment(\.finGoalTheme) private var theme

    private var fechaActual: String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "es_ES")
        formatter.dateFormat = "MMMM yyyy"

        let fecha = formatter.string(from: Date())

        return fecha.prefix(1).uppercased() + fecha.dropFirst()
    }

    var body: some View {
        HStack {
            Text("PERIODO")
                .foregroundStyle(theme.onSurfaceVariant)
                .font(.system(size: 15, weight: .bold))
                .tracking(1)

            Spacer()

            Text(fechaActual)
                .foregroundStyle(theme.primary)
                .font(.system(size: 20, weight: .bold))
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 12)
        .frame(maxWidth: .infinity)
        .background(theme.surfaceVariant)
        .clipShape(
            RoundedRectangle(cornerRadius: 12)
        )
    }
}