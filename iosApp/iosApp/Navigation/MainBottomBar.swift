import SwiftUI

struct MainBottomBar: View {

    @Environment(\.finGoalTheme) private var theme

    @Binding var selectedTab: MainTab

    var body: some View {
        HStack(spacing: 0) {

            tabButton(.dashboard)

            tabButton(.transactions)

            Spacer()
                .frame(width: 64)

            tabButton(.habits)

            tabButton(.goals)
        }
        .padding(.horizontal, 8)
        .padding(.top, 8)
        .padding(.bottom, 8)
        .background(
            theme.background
        )
        .overlay(
            Rectangle()
                .frame(height: 1)
                .foregroundStyle(
                    theme.outlineVariant
                ),
            alignment: .top
        )
    }

    @ViewBuilder
    private func tabButton(_ tab: MainTab) -> some View {

        Button {
            selectedTab = tab
        } label: {

            VStack(spacing: 4) {

                Image(systemName: tab.icon)
                    .font(.system(size: 20))

                Text(tab.label)
                    .font(.system(size: 12))
            }
            .foregroundStyle(
                selectedTab == tab
                    ? theme.primary
                    : theme.onSurfaceVariant
            )
            .frame(maxWidth: .infinity)
            .padding(.vertical, 4)
        }
        .buttonStyle(.plain)
    }
}