import SwiftUI

struct HabitItem: View {
    @Environment(\.finGoalTheme) private var theme

    let habit: Habit
    let onToggle: () -> Void
    let onDelete: () -> Void
    let onEdit: () -> Void
    let navigateToHabits: (() -> Void)?
    let isExpandable: Bool

    @State private var expanded = false

    init(
        habit: Habit,
        onToggle: @escaping () -> Void,
        onDelete: @escaping () -> Void,
        onEdit: @escaping () -> Void,
        navigateToHabits: (() -> Void)? = nil,
        isExpandable: Bool = true
    ) {
        self.habit = habit
        self.onToggle = onToggle
        self.onDelete = onDelete
        self.onEdit = onEdit
        self.navigateToHabits = navigateToHabits
        self.isExpandable = isExpandable
    }

    // MARK: - Frequency

    private var frequency: String {
        habit.frequency.uppercased()
    }

    private var frequencyData: (color: Color, icon: String, label: String) {
        switch frequency {
        case "DIARIO":
            return (
                Color(red: 0x81 / 255, green: 0xC7 / 255, blue: 0x84 / 255),
                "book.fill",
                "DIARIO"
            )

        case "SEMANAL":
            return (
                Color(red: 0x64 / 255, green: 0xB5 / 255, blue: 0xF6 / 255),
                "figure.walk",
                "SEMANAL"
            )

        default:
            return (
                Color(red: 0xBA / 255, green: 0x68 / 255, blue: 0xC8 / 255),
                "calendar",
                "MENSUAL"
            )
        }
    }

    // MARK: - Progress

    private var goal: Int {
        switch frequency {
        case "SEMANAL":
            return 7

        case "MENSUAL":
            return 30

        default:
            return 1
        }
    }

    private var progress: Double {
        if frequency == "DIARIO" {
            return habit.completedToday ? 1.0 : 0.0
        }

        return min(
            Double(habit.streak) / Double(goal),
            1.0
        )
    }

    private var percent: Int {
        Int(progress * 100)
    }

    private var isCompleted: Bool {
        if frequency == "DIARIO" {
            return habit.completedToday
        }

        return habit.streak >= goal
    }

    // MARK: - Status

    private var statusColor: Color {
        if habit.completedToday {
            return Color(
                red: 0xF5 / 255,
                green: 0x7C / 255,
                blue: 0x00 / 255
            )
        }

        return theme.outline
    }

    private var activeColor: Color {
        if isCompleted {
            return Color(
                red: 0x24 / 255,
                green: 0x9F / 255,
                blue: 0x29 / 255
            )
        }

        return statusColor
    }

    private var displayStreakText: String {
        if isCompleted {
            return "Completado"
        }

        return "Racha: \(habit.streak) días"
    }

    private var expenseColor: Color {
        Color(
            red: 0xE5 / 255,
            green: 0x39 / 255,
            blue: 0x35 / 255
        )
    }

    // MARK: - Body

    var body: some View {
        VStack(spacing: 0) {

            HStack(spacing: 0) {

                // Barra lateral según frecuencia
                Rectangle()
                    .fill(frequencyData.color)
                    .frame(width: 8)

                VStack(alignment: .leading, spacing: 0) {

                    // MARK: Header

                    HStack(alignment: .center) {

                        Image(systemName: frequencyData.icon)
                            .font(.system(size: 28))
                            .foregroundStyle(frequencyData.color)
                            .frame(width: 32)

                        Spacer()
                            .frame(width: 12)

                        VStack(alignment: .leading, spacing: 3) {

                            Text(habit.name)
                                .font(.system(
                                    size: 16,
                                    weight: .bold
                                ))
                                .foregroundStyle(theme.onSurface)

                            HStack(spacing: 0) {

                                Text("\(frequencyData.label) | ")
                                    .font(.system(size: 12))
                                    .foregroundStyle(frequencyData.color)

                                Text(displayStreakText)
                                    .font(.system(size: 12))
                                    .foregroundStyle(activeColor)
                            }
                        }
                        .frame(
                            maxWidth: .infinity,
                            alignment: .leading
                        )

                        // Racha + botón completar
                        VStack(spacing: 2) {

                            Text("RACHA")
                                .font(.system(
                                    size: 10,
                                    weight: .bold
                                ))
                                .foregroundStyle(activeColor)

                            Button {
                                if !habit.completedToday {
                                    onToggle()
                                }
                            } label: {
                                Image(systemName: "checkmark.circle.fill")
                                    .font(.system(size: 24))
                                    .foregroundStyle(activeColor)
                            }
                            .frame(width: 32, height: 32)
                            .disabled(
                                isCompleted && habit.completedToday
                            )
                        }
                    }

                    Spacer()
                        .frame(height: 12)

                    // MARK: Progress

                    HStack(spacing: 8) {

                        ProgressView(value: progress)
                            .tint(frequencyData.color)
                            .frame(maxWidth: .infinity)
                            .frame(height: 8)

                        Text("\(percent)%")
                            .font(.system(
                                size: 12,
                                weight: .bold
                            ))
                            .foregroundStyle(theme.onSurface)
                    }

                    // MARK: Expanded content

                    if isExpandable && expanded {
                        VStack(alignment: .leading, spacing: 0) {

                            Spacer()
                                .frame(height: 16)

                            Text(
                                habit.description.isEmpty
                                    ? "Sin descripción"
                                    : habit.description
                            )
                            .font(.system(size: 14))
                            .foregroundStyle(theme.onSurface)

                            HStack {
                                Spacer()

                                Button {
                                    onEdit()
                                } label: {
                                    Image(systemName: "pencil")
                                        .foregroundStyle(theme.primary)
                                        .frame(width: 40, height: 40)
                                }

                                Button {
                                    onDelete()
                                } label: {
                                    Image(systemName: "trash")
                                        .foregroundStyle(expenseColor)
                                        .frame(width: 40, height: 40)
                                }
                            }
                        }
                    }
                }
                .padding(16)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .background(theme.outlineVariant)
        .clipShape(
            RoundedRectangle(cornerRadius: 16)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(
                    Color.gray.opacity(0.25),
                    lineWidth: 0.5
                )
        )
        .padding(
            .vertical,
            4
        )
        .padding(
            .horizontal,
            navigateToHabits == nil ? 8 : 0
        )
        .contentShape(Rectangle())
        .onTapGesture {
            if let navigateToHabits {
                navigateToHabits()
            } else if isExpandable {
                withAnimation(.easeInOut(duration: 0.2)) {
                    expanded.toggle()
                }
            }
        }
    }
}