import SwiftUI

struct TransactionItem: View {
    @Environment(\.finGoalTheme) private var theme

    let transaction: Transaction
    let onEdit: () -> Void
    let onDelete: () -> Void

    @State private var isExpanded = false

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
        VStack(spacing: 0) {
            Button {
                withAnimation(.easeInOut(duration: 0.2)) {
                    isExpanded.toggle()
                }
            } label: {
                mainContent
            }
            .buttonStyle(.plain)

            if isExpanded {
                expandedContent
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity)
        .background(theme.outlineVariant)
        .clipShape(
            RoundedRectangle(cornerRadius: 12)
        )
        .shadow(
            color: .black.opacity(0.08),
            radius: 2,
            y: 1
        )
        .padding(.vertical, 4)
    }

    private var mainContent: some View {
        HStack(alignment: .center) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(
                        transactionColor.opacity(0.15)
                    )

                Image(systemName: categoryIcon)
                    .foregroundStyle(transactionColor)
            }
            .frame(width: 48, height: 48)

            Spacer()
                .frame(width: 16)

            VStack(
                alignment: .leading,
                spacing: 3
            ) {
                Text(transaction.title)
                    .font(
                        .system(
                            size: 16,
                            weight: .medium
                        )
                    )
                    .foregroundStyle(theme.onSurface)

                Text(transaction.category)
                    .font(.system(size: 14))
                    .foregroundStyle(theme.onSurfaceVariant)
            }

            Spacer()

            Text(
                transaction.isIncome
                    ? "+$\(transaction.amount)"
                    : "-$\(transaction.amount)"
            )
            .font(
                .system(
                    size: 16,
                    weight: .medium
                )
            )
            .foregroundStyle(transactionColor)
        }
    }

    private var expandedContent: some View {
        VStack(spacing: 8) {
            Divider()
                .overlay(theme.outlineVariant)
                .padding(.top, 4)

            HStack(alignment: .center) {
                VStack(
                    alignment: .leading,
                    spacing: 4
                ) {
                    if !transaction.description
                        .trimmingCharacters(
                            in: .whitespacesAndNewlines
                        )
                        .isEmpty {

                        Text("Descripción:")
                            .font(.system(size: 12))
                            .foregroundStyle(theme.primary)

                        Text(transaction.description)
                            .font(.system(size: 14))
                            .foregroundStyle(theme.onSurface)

                    } else {

                        Text("Sin descripción")
                            .font(.system(size: 14))
                            .foregroundStyle(
                                theme.onSurfaceVariant
                            )
                    }
                }

                Spacer()

                HStack(spacing: 4) {
                    Button(action: onEdit) {
                        Image(systemName: "pencil")
                            .foregroundStyle(theme.primary)
                            .frame(
                                width: 40,
                                height: 40
                            )
                    }

                    Button(action: onDelete) {
                        Image(systemName: "trash")
                            .foregroundStyle(expenseColor)
                            .frame(
                                width: 40,
                                height: 40
                            )
                    }
                }
            }
        }
        .padding(.top, 4)
    }

    private var transactionColor: Color {
        transaction.isIncome
            ? incomeColor
            : expenseColor
    }

    private var categoryIcon: String {
        switch transaction.category.lowercased() {
        case "alimentos":
            return "fork.knife"

        case "transporte":
            return "car.fill"

        case "tecnología":
            return "desktopcomputer"

        case "salud":
            return "heart.fill"

        case "entretenimiento":
            return "film"

        default:
            return "cart.fill"
        }
    }
}