import SwiftUI

struct GoalCard: View {

    @Environment(\.finGoalTheme) private var theme

    let goal: Goal
    let onClick: () -> Void

    @State private var imageError = false

    private let successColor = Color(
        red: 76 / 255,
        green: 175 / 255,
        blue: 80 / 255
    )

    private var progress: Double {
        guard goal.targetAmount > 0 else {
            return 0
        }

        return min(
            max(
                goal.currentAmount / goal.targetAmount,
                0
            ),
            1
        )
    }

    private var isCompleted: Bool {
        progress >= 1
    }

    var body: some View {
        Button {
            onClick()
        } label: {
            HStack {

                goalImage

                Spacer()
                    .frame(width: 16)

                VStack(alignment: .leading) {

                    Text(goal.title)
                        .font(
                            .system(
                                size: 17,
                                weight: .bold
                            )
                        )
                        .foregroundStyle(theme.onSurface)
                        .lineLimit(1)

                    HStack {

                        Image(systemName: "calendar")
                            .font(.system(size: 14))
                            .foregroundStyle(theme.onSurfaceVariant)

                        Text("Meta: \(goal.priority)")
                            .font(.caption)
                            .foregroundStyle(theme.onSurfaceVariant)
                    }

                    Spacer()
                        .frame(height: 8)

                    HStack {

                        Text(
                            "\(formatCurrency(goal.currentAmount)) / \(formatCurrency(goal.targetAmount))"
                        )
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundStyle(theme.onSurface)

                        Spacer()

                        Text(
                            isCompleted
                            ? "Completada"
                            : "\(Int(progress * 100))%"
                        )
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundStyle(
                            isCompleted
                            ? successColor
                            : theme.primary
                        )
                    }

                    ProgressView(value: progress)
                        .tint(
                            isCompleted
                            ? successColor
                            : theme.primary
                        )
                        .frame(height: 6)
                        .padding(.top, 4)
                }
            }
            .padding(12)
            .frame(maxWidth: .infinity)
            .background(theme.surfaceVariant)
            .clipShape(
                RoundedRectangle(
                    cornerRadius: 16
                )
            )
            .shadow(
                color: .black.opacity(0.08),
                radius: 2
            )
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var goalImage: some View {

        if goal.localImagePath.isEmpty || imageError {

            Image("metas")
                .resizable()
                .scaledToFill()
                .frame(width: 80, height: 80)
                .clipShape(
                    RoundedRectangle(
                        cornerRadius: 12
                    )
                )

        } else {

            // Lo conectamos con la ruta real
            // cuando veamos cómo se guarda localImagePath.

            Image("metas")
                .resizable()
                .scaledToFill()
                .frame(width: 80, height: 80)
                .clipShape(
                    RoundedRectangle(
                        cornerRadius: 12
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