import SwiftUI

struct DashboardView: View {

    let totalBalance: Double
    let totalExpenses: Double
    let savingsTotal: Double
    let chartData: [Float]

    let recentHabits: [Habit]?
    let topGoal: Goal?

    let onNavigateToTransactions: () -> Void
    let onNavigateToHabits: () -> Void
    let onNavigateToGoals: () -> Void

    let isDarkMode: Bool
    let onToggleDarkMode: () -> Void

    var body: some View {
        ScrollView {
            VStack(
                alignment: .leading,
                spacing: 16
            ) {

                BalanceCard(
                    totalBalance: totalBalance,
                    isDarkMode: isDarkMode,
                    onToggleDarkMode: onToggleDarkMode,
                    onNavigateToTransactions:
                        onNavigateToTransactions
                )

                DashboardSummaryCards(
                    totalExpenses: totalExpenses,
                    savingsTotal: savingsTotal
                )

                if !chartData.isEmpty {
                    Text("Flujo del mes")
                        .font(.headline)
                        .fontWeight(.bold)
                        .padding(.top, 8)

                    BalanceChart(
                        chartData: chartData
                    )
                    .padding(16)
                    .frame(maxWidth: .infinity)
                    .background(
                        Color(uiColor: .secondarySystemBackground)
                    )
                    .clipShape(
                        RoundedRectangle(
                            cornerRadius: 12
                        )
                    )
                    .shadow(
                        color: .black.opacity(0.08),
                        radius: 2
                    )
                }

                if let habits = recentHabits,
                   !habits.isEmpty {

                    Text("Hábitos de hoy")
                        .font(.headline)
                        .fontWeight(.bold)

                    ForEach(habits, id: \.remoteId) { habit in
                        HabitItem(
                            habit: habit,
                            onToggle: {},
                            onDelete: {},
                            onEdit: {},
                            navigateToHabits: onNavigateToHabits,
                            isExpandable: false
                        )
                    }
                }

                if let goal = topGoal {
                    Text("Próximo objetivo")
                        .font(.headline)
                        .fontWeight(.bold)

                    GoalCard(
                        goal: goal,
                        onClick: onNavigateToGoals
                    )
                }

                Spacer()
                    .frame(height: 16)
            }
            .padding(16)
        }
    }
}