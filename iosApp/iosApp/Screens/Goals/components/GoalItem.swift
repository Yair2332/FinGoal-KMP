import SwiftUI

struct GoalItem: View {

    @Environment(\.finGoalTheme) private var theme

    let goal: Goal
    let onDelete: () -> Void
    let onEdit: () -> Void
    let onContribution: (
        _ amount: Double,
        _ isAdding: Bool
    ) -> Void

    @State private var expanded = false
    @State private var showContributionDialog = false
    @State private var imageError = false

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
        goal.currentAmount >= goal.targetAmount
    }

    private var hasImagePath: Bool {
        !goal.localImagePath.isEmpty
    }

    private var successColor: Color {
        theme.isDarkMode
            ? Color(
                red: 0x81 / 255,
                green: 0xC7 / 255,
                blue: 0x84 / 255
            )
            : Color(
                red: 0x38 / 255,
                green: 0x8E / 255,
                blue: 0x3C / 255
            )
    }

    private let deleteColor = Color(
        red: 0xE5 / 255,
        green: 0x39 / 255,
        blue: 0x35 / 255
    )

    var body: some View {

        VStack(spacing: 0) {

            // MARK: - Image

            ZStack(alignment: .topTrailing) {

                if hasImagePath && !imageError,
                   let url = URL(
                    string: goal.localImagePath
                   ) {

                    AsyncImage(url: url) { phase in

                        switch phase {

                        case .empty:
                            ProgressView()
                                .frame(
                                    maxWidth: .infinity
                                )
                                .frame(height: 160)

                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()
                                .frame(
                                    maxWidth: .infinity
                                )
                                .frame(height: 160)
                                .clipped()

                        case .failure:
                            fallbackImage

                        @unknown default:
                            fallbackImage
                        }
                    }

                } else {
                    fallbackImage
                }

                // MARK: Completed overlay

                if isCompleted {

                    Rectangle()
                        .fill(
                            successColor.opacity(0.6)
                        )
                        .frame(height: 160)
                        .overlay {

                            Text("COMPLETADO")
                                .foregroundStyle(.white)
                                .font(.headline)
                                .fontWeight(.bold)
                        }
                }

                // MARK: Target amount

                Text("$\(Int(goal.targetAmount))")
                    .fontWeight(.bold)
                    .foregroundStyle(theme.primary)
                    .padding(
                        .horizontal,
                        8
                    )
                    .padding(
                        .vertical,
                        4
                    )
                    .background(
                        theme.surface.opacity(0.85)
                    )
                    .clipShape(
                        RoundedRectangle(
                            cornerRadius: 8
                        )
                    )
                    .padding(12)
            }

            // MARK: - Information

            VStack(
                alignment: .leading,
                spacing: 12
            ) {

                HStack(
                    alignment: .top
                ) {

                    VStack(
                        alignment: .leading,
                        spacing: 4
                    ) {

                        Text(goal.title)
                            .font(.title3)
                            .fontWeight(.bold)
                            .foregroundStyle(
                                theme.onSurface
                            )

                        Text(
                            "Meta: \(goal.description)"
                        )
                        .font(.body)
                        .foregroundStyle(
                            theme.onSurfaceVariant
                        )
                    }
                    .frame(
                        maxWidth: .infinity,
                        alignment: .leading
                    )
                    .padding(.trailing, 8)

                    VStack(
                        alignment: .trailing,
                        spacing: 2
                    ) {

                        Text(
                            "$\(Int(goal.currentAmount))"
                        )
                        .font(.title3)
                        .fontWeight(.bold)
                        .foregroundStyle(
                            isCompleted
                                ? successColor
                                : theme.primary
                        )

                        Text("Ahorrado")
                            .font(.caption)
                            .foregroundStyle(
                                theme.onSurfaceVariant
                            )
                    }
                }

                // MARK: Progress

                ProgressView(
                    value: progress
                )
                .tint(
                    isCompleted
                        ? successColor
                        : theme.primary
                )
                .frame(height: 8)

                // MARK: Progress information

                HStack {

                    Text(
                        isCompleted
                            ? "¡Meta Completada!"
                            : "\(Int(progress * 100))% completado"
                    )
                    .font(.caption)
                    .foregroundStyle(
                        isCompleted
                            ? successColor
                            : theme.onSurfaceVariant
                    )

                    Spacer()

                    if !isCompleted {

                        Text(
                            "Faltan $\(Int(goal.targetAmount - goal.currentAmount))"
                        )
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundStyle(
                            theme.primary
                        )
                    }
                }

                // MARK: Expanded actions

                if expanded {

                    HStack {

                        Button {
                            onEdit()
                        } label: {
                            Label(
                                "Editar",
                                systemImage: "pencil"
                            )
                            .foregroundStyle(
                                theme.primary
                            )
                        }

                        Spacer()

                        Button {
                            showContributionDialog = true
                        } label: {
                            Label(
                                "Aportar",
                                systemImage: "plus"
                            )
                            .foregroundStyle(
                                theme.primary
                            )
                        }

                        Spacer()

                        Button(
                            role: .destructive
                        ) {
                            onDelete()
                        } label: {
                            Label(
                                "Borrar",
                                systemImage: "trash"
                            )
                        }
                    }
                    .padding(8)
                    .background(
                        theme.surfaceVariant
                            .opacity(0.3)
                    )
                    .clipShape(
                        RoundedRectangle(
                            cornerRadius: 12
                        )
                    )
                }
            }
            .padding(16)
        }
        .background(theme.outlineVariant)
        .clipShape(
            RoundedRectangle(
                cornerRadius: 12
            )
        )
        .shadow(
            color: .black.opacity(0.08),
            radius: 3,
            x: 0,
            y: 1
        )
        .contentShape(Rectangle())
        .onTapGesture {
            withAnimation(.easeInOut(duration: 0.2)) {
                expanded.toggle()
            }
        }
        .sheet(
            isPresented: $showContributionDialog
        ) {

            ContributionDialog(
                currentAmount: goal.currentAmount,
                targetAmount: goal.targetAmount,

                onDismiss: {
                    showContributionDialog = false
                },

                onConfirm: {
                    amount,
                    isAdding in

                    onContribution(
                        amount,
                        isAdding
                    )

                    showContributionDialog = false
                }
            )
            .presentationDetents([.medium])
        }
    }

    // MARK: - Fallback image

    private var fallbackImage: some View {

        Image("metas")
            .resizable()
            .scaledToFill()
            .frame(
                maxWidth: .infinity
            )
            .frame(height: 160)
            .clipped()
    }
}