import SwiftUI

enum MainTab: CaseIterable {

    case dashboard
    case transactions
    case habits
    case goals

    var title: String {
        switch self {
        case .dashboard:
            return "FinGoal"

        case .transactions:
            return "Transacciones"

        case .habits:
            return "Hábitos"

        case .goals:
            return "Mis Metas"
        }
    }

    var label: String {
        switch self {
        case .dashboard:
            return "Panel"

        case .transactions:
            return "Flujo"

        case .habits:
            return "Hábitos"

        case .goals:
            return "Metas"
        }
    }

    var icon: String {
        switch self {
        case .dashboard:
            return "house.fill"

        case .transactions:
            return "creditcard.fill"

        case .habits:
            return "checklist"

        case .goals:
            return "banknote.fill"
        }
    }

    var hasHelp: Bool {
        switch self {
        case .dashboard:
            return false

        case .transactions,
             .habits,
             .goals:
            return true
        }
    }
}