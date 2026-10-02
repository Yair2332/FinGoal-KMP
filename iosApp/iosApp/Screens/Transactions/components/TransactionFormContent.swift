import SwiftUI

struct TransactionFormContent: View {
    @Environment(\.finGoalTheme) private var theme

    let initialTransaction: Transaction?

    let onSave: (
        String,
        Double,
        String,
        Bool,
        String
    ) -> Void

    @State private var title: String
    @State private var description: String
    @State private var amount: String
    @State private var category: String
    @State private var isIncome: Bool
    @State private var isError = false

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

    init(
        initialTransaction: Transaction? = nil,
        onSave: @escaping (
            String,
            Double,
            String,
            Bool,
            String
        ) -> Void
    ) {
        self.initialTransaction = initialTransaction
        self.onSave = onSave

        _title = State(
            initialValue: initialTransaction?.title ?? ""
        )

        _description = State(
            initialValue: initialTransaction?.description ?? ""
        )

        _amount = State(
            initialValue: initialTransaction.map {
                String($0.amount)
            } ?? ""
        )

        _category = State(
            initialValue:
                initialTransaction?.category.uppercased()
                ?? "OTROS"
        )

        _isIncome = State(
            initialValue:
                initialTransaction?.isIncome ?? true
        )
    }

    var body: some View {
        ScrollView {
            VStack(
                alignment: .leading,
                spacing: 20
            ) {

                HStack {
                    Image(systemName: "plus")
                        .foregroundStyle(theme.primary)
                        .font(.system(size: 28))

                    Text(
                        initialTransaction == nil
                            ? "Nueva Transacción"
                            : "Editar Transacción"
                    )
                    .font(
                        .system(
                            size: 26,
                            weight: .bold
                        )
                    )
                    .foregroundStyle(theme.onSurface)

                    Spacer()
                }

                typeSelector

                TextField(
                    "Título",
                    text: $title
                )
                .textFieldStyle(.roundedBorder)

                TextField(
                    "Monto ($)",
                    text: $amount
                )
                .keyboardType(.decimalPad)
                .textFieldStyle(.roundedBorder)

                categorySelector

                TextField(
                    "Descripción (Opcional)",
                    text: $description,
                    axis: .vertical
                )
                .lineLimit(3...5)
                .textFieldStyle(.roundedBorder)

                if isError {
                    Text(
                        "Por favor, completa correctamente los campos obligatorios."
                    )
                    .font(.footnote)
                    .foregroundStyle(.red)
                }

                Button {
                    save()
                } label: {
                    Text("Guardar Transacción")
                        .fontWeight(.bold)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                }
                .foregroundStyle(theme.onPrimary)
                .background(theme.primary)
                .clipShape(
                    RoundedRectangle(cornerRadius: 12)
                )
            }
            .padding(24)
        }
    }

    private var typeSelector: some View {
        HStack(spacing: 0) {
            selectorButton(
                title: "Ingreso",
                color: incomeColor,
                selected: isIncome
            ) {
                isIncome = true
            }

            selectorButton(
                title: "Gasto",
                color: expenseColor,
                selected: !isIncome
            ) {
                isIncome = false
            }
        }
        .padding(4)
        .frame(height: 56)
        .background(theme.surfaceVariant)
        .clipShape(
            RoundedRectangle(cornerRadius: 12)
        )
    }

    private func selectorButton(
        title: String,
        color: Color,
        selected: Bool,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            Text(title)
                .fontWeight(.bold)
                .foregroundStyle(
                    selected
                        ? .white
                        : theme.onSurfaceVariant
                )
                .frame(
                    maxWidth: .infinity
                )
                .frame(
                    maxHeight: .infinity
                )
                .background(
                    selected
                        ? color
                        : Color.clear
                )
                .clipShape(
                    RoundedRectangle(
                        cornerRadius: 8
                    )
                )
        }
        .buttonStyle(.plain)
    }

    private var categorySelector: some View {
        VStack(
            alignment: .leading,
            spacing: 10
        ) {
            Text("Categoría")
                .font(.subheadline)
                .foregroundStyle(
                    theme.onSurfaceVariant
                )

            ScrollView(
                .horizontal,
                showsIndicators: false
            ) {
                HStack(spacing: 8) {
                    ForEach(
                        transactionCategories
                    ) { category in

                        let selected =
                            self.category.uppercased()
                            == category.name.uppercased()

                        Button {
                            self.category =
                                category.name.uppercased()
                        } label: {
                            HStack(spacing: 6) {
                                Image(
                                    systemName:
                                        category.icon
                                )

                                Text(category.name)
                            }
                            .font(.subheadline)
                            .foregroundStyle(
                                selected
                                    ? theme.onPrimary
                                    : theme.onSurfaceVariant
                            )
                            .padding(.horizontal, 12)
                            .padding(.vertical, 9)
                            .background(
                                selected
                                    ? theme.primary
                                    : theme.surfaceVariant
                            )
                            .clipShape(
                                Capsule()
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
        }
    }

    private func save() {
        let parsedAmount = Double(
            amount.replacingOccurrences(
                of: ",",
                with: "."
            )
        ) ?? 0

        let validTitle =
            !title
                .trimmingCharacters(
                    in: .whitespacesAndNewlines
                )
                .isEmpty

        if validTitle &&
            parsedAmount > 0 &&
            !category.isEmpty {

            isError = false

            onSave(
                title,
                parsedAmount,
                category,
                isIncome,
                description
            )

        } else {
            isError = true
        }
    }
}

struct TransactionCategory: Identifiable {
    let id = UUID()
    let name: String
    let icon: String
}

let transactionCategories: [TransactionCategory] = [
    TransactionCategory(
        name: "Alimentos",
        icon: "fork.knife"
    ),
    TransactionCategory(
        name: "Transporte",
        icon: "car.fill"
    ),
    TransactionCategory(
        name: "Tecnología",
        icon: "desktopcomputer"
    ),
    TransactionCategory(
        name: "Salud",
        icon: "heart.fill"
    ),
    TransactionCategory(
        name: "Entretenimiento",
        icon: "film"
    ),
    TransactionCategory(
        name: "Otros",
        icon: "cart.fill"
    )
]