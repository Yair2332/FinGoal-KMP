import SwiftUI

struct HabitHeader: View {

    @Environment(\.finGoalTheme) private var theme

    let phrases: [String]

    var body: some View {
        VStack(spacing: 8) {

            Spacer()
                .frame(height: 16)

            AnimatedMotivationalCard(
                imageName: "habitos",
                phrases: phrases,
                shadowColor: theme.primary,
                interval: 5.0
            )

            Spacer()
                .frame(height: 8)
        }
    }
}