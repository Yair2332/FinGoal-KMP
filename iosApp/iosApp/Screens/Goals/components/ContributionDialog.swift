import SwiftUI

struct ContributionDialog: View {

    let currentAmount: Double
    let targetAmount: Double

    let onDismiss: () -> Void

    let onConfirm: (
        _ amount: Double,
        _ isAdding: Bool
    ) -> Void

    @State private var amount = ""

    private var parsedAmount: Double {
        Double(amount) ?? 0.0
    }

    private var isPositive: Bool {
        parsedAmount > 0
    }

    private var exceedsGoal: Bool {
        currentAmount + parsedAmount > targetAmount
    }

    private var exceedsBalance: Bool {
        parsedAmount > currentAmount
    }

    private var showError: Bool {
        !amount.isEmpty &&
        (!isPositive || exceedsGoal)
    }

    var body: some View {

        NavigationStack {

            VStack(
                alignment: .leading,
                spacing: 16
            ) {

                Text("Gestionar ahorros")
                    .font(.headline)
                    .fontWeight(.bold)

                TextField(
                    "Monto ($)",
                    text: $amount
                )
                .keyboardType(.decimalPad)
                .textFieldStyle(.roundedBorder)

                if showError {

                    Text(
                        !isPositive
                            ? "El monto debe ser mayor a 0"
                            : "Superas el límite de la meta"
                    )
                    .font(.footnote)
                    .foregroundStyle(.red)
                }

                Text(
                    "Restan: $\(Int(targetAmount - currentAmount))"
                )
                .font(.caption)
                .fontWeight(.medium)
                .foregroundStyle(.secondary)

                HStack(spacing: 8) {

                    Button("Aportar") {

                        onConfirm(
                            parsedAmount,
                            true
                        )
                    }
                    .buttonStyle(
                        .borderedProminent
                    )
                    .disabled(
                        !isPositive ||
                        exceedsGoal
                    )

                    Button("Retirar") {

                        onConfirm(
                            parsedAmount,
                            false
                        )
                    }
                    .buttonStyle(.bordered)
                    .disabled(
                        !isPositive ||
                        exceedsBalance
                    )
                }

                Spacer()
            }
            .padding(24)
            .toolbar {

                ToolbarItem(
                    placement: .cancellationAction
                ) {

                    Button("Cancelar") {
                        onDismiss()
                    }
                }
            }
        }
    }
}