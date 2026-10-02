import SwiftUI

struct FinGoalTheme {

    let isDarkMode: Bool

    var primary: Color {
        isDarkMode
            ? FinGoalColors.primaryDarkVibrant
            : FinGoalColors.primaryLight
    }

    var secondary: Color {
        isDarkMode
            ? FinGoalColors.secondaryDarkVibrant
            : FinGoalColors.secondaryLight
    }

    var tertiary: Color {
        isDarkMode
            ? FinGoalColors.tertiaryDark
            : FinGoalColors.tertiaryLight
    }

    var background: Color {
        isDarkMode
            ? FinGoalColors.neutralDark
            : Color.white
    }

    var surface: Color {
        isDarkMode
            ? FinGoalColors.surfaceDark
            : Color.white
    }

    var surfaceVariant: Color {
        isDarkMode
            ? Color(
                red: 0x1E / 255.0,
                green: 0x1E / 255.0,
                blue: 0x1E / 255.0
            )
            : FinGoalColors.neutralLight
    }

    var onSurface: Color {
        isDarkMode
            ? Color(
                red: 0xE0 / 255.0,
                green: 0xE0 / 255.0,
                blue: 0xE0 / 255.0
            )
            : Color.black
    }

    var onSurfaceVariant: Color {
        isDarkMode
            ? Color(
                red: 0xE7 / 255.0,
                green: 0xE7 / 255.0,
                blue: 0xE7 / 255.0
            )
            : Color.black
    }

    var outline: Color {
        isDarkMode
            ? FinGoalColors.outlineDark
            : FinGoalColors.outlineLight
    }

    var outlineVariant: Color {
        isDarkMode
            ? Color(
                red: 0x1A / 255.0,
                green: 0x1A / 255.0,
                blue: 0x1A / 255.0
            )
            : Color.white
    }

    var onPrimary: Color {
        isDarkMode
            ? Color.black
            : Color.white
    }

    var onSecondary: Color {
        isDarkMode
            ? Color.white
            : Color.black
    }
}