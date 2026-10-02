import SwiftUI

struct MainAppNavigation: View {

    let onLogout: () -> Void

    @State private var selectedTab: MainTab = .dashboard
    @State private var isDarkMode = false

    var body: some View {
        ZStack(alignment: .bottom) {

            VStack(spacing: 0) {

                MainTopBar(
                    title: selectedTab.title,
                    showHelp: selectedTab.hasHelp,
                    onHelp: {
                        // después
                    },
                    onLogout: onLogout
                )

                contentView
                    .frame(
                        maxWidth: .infinity,
                        maxHeight: .infinity
                    )

                MainBottomBar(
                    selectedTab: $selectedTab
                )
            }

            MainFloatingButton {
                handleFloatingAction()
            }
            .offset(y: -28)
        }
        .preferredColorScheme(
            isDarkMode ? .dark : .light
        )
        .environment(
            \.finGoalTheme,
            FinGoalTheme(
                isDarkMode: isDarkMode
            )
        )
    }

    @ViewBuilder
    private var contentView: some View {

        switch selectedTab {

        case .dashboard:

            DashboardView(
                totalBalance: 0,
                totalExpenses: 0,
                savingsTotal: 0,
                chartData: [],
                recentHabits: nil,
                topGoal: nil,
                onNavigateToTransactions: {
                    selectedTab = .transactions
                },
                onNavigateToHabits: {
                    selectedTab = .habits
                },
                onNavigateToGoals: {
                    selectedTab = .goals
                },
                isDarkMode: isDarkMode,
                onToggleDarkMode: {
                    isDarkMode.toggle()
                }
            )

        case .transactions:

            TransactionsView()

        case .habits:

            HabitsView()

        case .goals:

            GoalsView()
        }
    }

    private func handleFloatingAction() {

        switch selectedTab {

        case .dashboard:

            selectedTab = .transactions

            // Más adelante:
            // transactionViewModel.showAddSheet()

        case .transactions:

            // Más adelante:
            // transactionViewModel.showAddSheet()
            break

        case .habits:

            // Más adelante:
            // habitViewModel.showAddDialog()
            break

        case .goals:

            // Más adelante:
            // showGoalDialog = true
            break
        }
    }
}