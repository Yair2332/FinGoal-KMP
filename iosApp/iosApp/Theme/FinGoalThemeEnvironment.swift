import SwiftUI

private struct FinGoalThemeKey: EnvironmentKey {

    static let defaultValue = FinGoalTheme(
        isDarkMode: false
    )
}

extension EnvironmentValues {

    var finGoalTheme: FinGoalTheme {
        get {
            self[FinGoalThemeKey.self]
        }

        set {
            self[FinGoalThemeKey.self] = newValue
        }
    }
}