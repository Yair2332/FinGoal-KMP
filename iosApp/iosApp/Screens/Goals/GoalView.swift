import SwiftUI

struct GoalsView: View {

    @Environment(\.finGoalTheme) private var theme

    // KMP se conectará después.
    let goals: [Goal]
    let isLoading: Bool

    let onDelete: (Goal) -> Void
    let onEdit: (Goal) -> Void

    let onContribution: (
        _ goal: Goal,
        _ amount: Double,
        _ isAdding: Bool
    ) -> Void

    let onSaveGoal: (
        _ goal: Goal?,
        _ title: String,
        _ description: String,
        _ amount: Double,
        _ image: String
    ) -> Void

    @State private var showBottomSheet = false
    @State private var goalToEdit: Goal?

    init(
        goals: [Goal] = [],
        isLoading: Bool = false,
        onDelete: @escaping (Goal) -> Void = { _ in },
        onEdit: @escaping (Goal) -> Void = { _ in },
        onContribution: @escaping (
            Goal,
            Double,
            Bool
        ) -> Void = { _, _, _ in },
        onSaveGoal: @escaping (
            Goal?,
            String,
            String,
            Double,
            String
        ) -> Void = { _, _, _, _, _ in }
    ) {
        self.goals = goals
        self.isLoading = isLoading
        self.onDelete = onDelete
        self.onEdit = onEdit
        self.onContribution = onContribution
        self.onSaveGoal = onSaveGoal
    }

    var body: some View {

        ZStack {

            theme.background
                .ignoresSafeArea()

            ScrollView {

                LazyVStack(
                    spacing: 12
                ) {

                    // MARK: Motivational card

                    AnimatedMotivationalCard(
                        imageName: "metas_target",
                        phrases: [
                            "Una meta sin un plan es solo un deseo.",
                            "El éxito es el resultado de metas claras."
                        ],
                        shadowColor: Color(
                            red: 0x00 / 255,
                            green: 0x56 / 255,
                            blue: 0xB3 / 255
                        ),
                        interval: 5.0
                    )
                    .padding(.top, 16)

                    // MARK: Loading

                    if isLoading {

                        ProgressView()
                            .tint(theme.primary)
                            .frame(
                                maxWidth: .infinity,
                                minHeight: 250
                            )

                    // MARK: Empty

                    } else if goals.isEmpty {

                        EmptyStateView(
                            message:
                                "¡No tienes metas aún! Pulsa el botón de abajo para registrar tu primer meta."
                        )
                        .padding(.top, 48)

                    // MARK: Goals

                    } else {

                        ForEach(
                            goals,
                            id: \.remoteId
                        ) { goal in

                            GoalItem(
                                goal: goal,

                                onDelete: {
                                    onDelete(goal)
                                },

                                onEdit: {
                                    goalToEdit = goal
                                    showBottomSheet = true
                                    onEdit(goal)
                                },

                                onContribution: {
                                    amount,
                                    isAdding in

                                    onContribution(
                                        goal,
                                        amount,
                                        isAdding
                                    )
                                }
                            )
                        }
                    }

                    Spacer()
                        .frame(height: 80)
                }
                .padding(.horizontal, 8)
            }
        }

        // MARK: Goal form

        .sheet(
            isPresented: $showBottomSheet,
            onDismiss: {
                goalToEdit = nil
            }
        ) {

            GoalFormContent(
                goal: goalToEdit,

                onSave: {
                    title,
                    description,
                    amount,
                    image in

                    onSaveGoal(
                        goalToEdit,
                        title,
                        description,
                        amount,
                        image
                    )

                    showBottomSheet = false
                    goalToEdit = nil
                }
            )
            .presentationDetents([.large])
        }
    }
}