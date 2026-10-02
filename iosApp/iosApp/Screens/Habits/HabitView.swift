import SwiftUI

struct HabitsView: View {

    @Environment(\.finGoalTheme) private var theme

    // KMP se conectará después.
    let habits: [Habit]
    let isLoading: Bool

    let onToggle: (Habit) -> Void
    let onDelete: (Habit) -> Void
    let onEdit: (Habit) -> Void

    let onCreateHabit: (
        _ name: String,
        _ description: String,
        _ frequency: String
    ) -> Void

    let onUpdateHabit: (
        _ habit: Habit,
        _ name: String,
        _ description: String,
        _ frequency: String
    ) -> Void

    @State private var showHabitDialog = false
    @State private var habitToEdit: Habit?

    @State private var habitToDelete: Habit?
    @State private var showDeleteDialog = false

    private let phrases = [
        "Tus hábitos determinan tu futuro financiero. ¡Construye hoy el mañana que deseas!",
        "No es cuánto ganas, sino cómo administras lo que tienes lo que marca la diferencia.",
        "El ahorro es la base de la libertad financiera y la tranquilidad personal.",
        "La disciplina financiera es el puente entre tus objetivos y tus logros."
    ]

    private let frequencyOrder = [
        "DIARIO",
        "SEMANAL",
        "MENSUAL"
    ]

    init(
        habits: [Habit] = [],
        isLoading: Bool = false,
        onToggle: @escaping (Habit) -> Void = { _ in },
        onDelete: @escaping (Habit) -> Void = { _ in },
        onEdit: @escaping (Habit) -> Void = { _ in },
        onCreateHabit: @escaping (
            String,
            String,
            String
        ) -> Void = { _, _, _ in },
        onUpdateHabit: @escaping (
            Habit,
            String,
            String,
            String
        ) -> Void = { _, _, _, _ in }
    ) {
        self.habits = habits
        self.isLoading = isLoading
        self.onToggle = onToggle
        self.onDelete = onDelete
        self.onEdit = onEdit
        self.onCreateHabit = onCreateHabit
        self.onUpdateHabit = onUpdateHabit
    }

    // MARK: - Grouping

    private var groupedHabits: [String: [Habit]] {
        Dictionary(
            grouping: habits
        ) {
            $0.frequency.uppercased()
        }
    }

    // MARK: - Body

    var body: some View {
        ZStack {

            theme.background
                .ignoresSafeArea()

            ScrollView {
                LazyVStack(
                    spacing: 12
                ) {

                    // MARK: Header

                    HabitHeader(
                        phrases: phrases
                    )

                    // MARK: Loading

                    if isLoading {

                        ProgressView()
                            .tint(theme.primary)
                            .frame(
                                maxWidth: .infinity,
                                minHeight: 250
                            )

                    // MARK: Empty

                    } else if habits.isEmpty {

                        EmptyStateView(
                            message:
                                "¡No tienes hábitos aún! Pulsa el botón para registrar tu primer hábito."
                        )
                        .padding(.top, 48)

                    // MARK: Habits

                    } else {

                        ForEach(
                            frequencyOrder,
                            id: \.self
                        ) { frequency in

                            if let frequencyHabits =
                                groupedHabits[frequency],
                               !frequencyHabits.isEmpty {

                                // MARK: Section header

                                VStack(
                                    alignment: .leading,
                                    spacing: 1
                                ) {

                                    Text(
                                        displayFrequency(
                                            frequency
                                        )
                                    )
                                    .font(.title3)
                                    .fontWeight(.bold)
                                    .foregroundStyle(theme.onSurface)

                                    Divider()
                                        .overlay(
                                            theme.outlineVariant
                                        )
                                }
                                .padding(.horizontal, 8)
                                .padding(.top, 4)

                                // MARK: Items

                                ForEach(
                                    frequencyHabits,
                                    id: \.remoteId
                                ) { habit in

                                    HabitItem(
                                        habit: habit,

                                        onToggle: {
                                            onToggle(habit)
                                        },

                                        onDelete: {
                                            habitToDelete = habit
                                            showDeleteDialog = true
                                        },

                                        onEdit: {
                                            habitToEdit = habit
                                            showHabitDialog = true
                                            onEdit(habit)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer()
                        .frame(height: 80)
                }
                .padding(.horizontal, 8)
            }
        }

        // MARK: Add / Edit dialog

        .sheet(
            isPresented: $showHabitDialog,
            onDismiss: {
                habitToEdit = nil
            }
        ) {
            AddHabitDialog(
                onDismiss: {
                    showHabitDialog = false
                    habitToEdit = nil
                },

                onConfirm: {
                    name,
                    description,
                    frequency in

                    if let habit = habitToEdit {

                        onUpdateHabit(
                            habit,
                            name,
                            description,
                            frequency
                        )

                    } else {

                        onCreateHabit(
                            name,
                            description,
                            frequency
                        )
                    }

                    showHabitDialog = false
                    habitToEdit = nil
                },

                initialTitle:
                    habitToEdit?.name ?? "",

                initialDescription:
                    habitToEdit?.description ?? "",

                initialFrequency:
                    habitToEdit?.frequency ?? "DIARIO"
            )
            .presentationDetents([
                .medium,
                .large
            ])
        }

        // MARK: Delete confirmation

        .confirmationDialog(
            "¿Eliminar hábito?",
            isPresented: $showDeleteDialog,
            titleVisibility: .visible
        ) {

            Button(
                "Eliminar",
                role: .destructive
            ) {

                if let habit = habitToDelete {
                    onDelete(habit)
                }

                habitToDelete = nil
            }

            Button(
                "Cancelar",
                role: .cancel
            ) {
                habitToDelete = nil
            }

        } message: {

            Text(
                "¿Seguro que quieres eliminar '\(habitToDelete?.name ?? "")'?"
            )
        }
    }

    // MARK: - Helpers

    private func displayFrequency(
        _ frequency: String
    ) -> String {
        frequency
            .lowercased()
            .prefix(1)
            .uppercased()
        +
        frequency
            .lowercased()
            .dropFirst()
    }
}